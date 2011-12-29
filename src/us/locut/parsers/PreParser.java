package us.locut.parsers;

import java.io.*;
import java.util.*;

import com.google.common.collect.*;

import us.locut.Misc;

public class PreParser extends Parser {
	public static PreParser singleton = new PreParser();

	private static final long serialVersionUID = 3705611710430408505L;

	private static ArrayList<Object> template;

	public static Set<String> reserved = Sets.newHashSet("(", ")", "{", "}", "[", "]", ",", ":", "...");

	static {
		template = Lists.newArrayList();
		template.add(Lists.<Object> newArrayList(")", "]", "}"));
	}

	public static List<Object> flatten(final List<Object> input) {
		boolean willFlatten = false;
		for (final Object o : input) {
			if (o instanceof SubTokenSequence) {
				willFlatten = true;
				break;
			}
			if (o instanceof List) {
				willFlatten = true;
				break;
			}
			if (o instanceof Map) {
				willFlatten = true;
				break;
			}
			if (o instanceof ListWithTail || o instanceof MapWithTail) {
				willFlatten = true;
				break;
			}
		}
		if (!willFlatten)
			return Lists.newArrayList(input);

		final ArrayList<Object> ret = Lists.newArrayListWithCapacity(input.size() * 4);

		for (final Object o : input) {
			if ((o instanceof SubTokenSequence) && ((SubTokenSequence) o).tokens.size() > 1) {
				ret.add("(");
				flattenTo(o, ret);
				ret.add(")");
			} else {
				flattenTo(o, ret);
			}
		}

		return ret;
	}

	private static void flattenTo(final Object obj, final ArrayList<Object> output) {
		if (obj instanceof SubTokenSequence) {
			final SubTokenSequence sts = (SubTokenSequence) obj;
			if (sts.tokens.size() == 1) {
				flattenTo(sts.tokens.get(0), output);
			} else {
				for (final Object o : sts.tokens) {
					flattenTo(o, output);
				}
			}
		} else if (obj instanceof List) {
			final List<Object> list = (List<Object>) obj;
			
				output.add("[");
				for (final Object lo : list) {
					flattenTo(lo, output);
					output.add(",");
				}
				if (list.isEmpty()) {
					output.add("]");
				} else {
				// Overwrite last ","
				output.set(output.size() - 1, "]");
				}
		} else if (obj instanceof Map) {
			final Map<Object, Object> map = (Map<Object, Object>) obj;

			output.add("{");
			for (final Map.Entry<Object, Object> e : map.entrySet()) {
				flattenTo(e.getKey(), output);
				output.add(":");
				flattenTo(e.getValue(), output);
				output.add(",");
			}
			if (map.isEmpty()) {
				output.add("}");
			} else {
				output.set(output.size() - 1, "}");
			}
		} else if (obj instanceof ListWithTail) {
			final ListWithTail lwt = (ListWithTail) obj;
			output.add("[");
			for (final Object lo : lwt.list) {
				flattenTo(lo, output);
				output.add(",");
			}
			output.set(output.size() - 1, "...");
			flattenTo(lwt.tail, output);
			output.add("]");
		} else if (obj instanceof MapWithTail) {
			final MapWithTail mwt = (MapWithTail) obj;

			output.add("{");
			for (final Map.Entry<Object, Object> e : mwt.map.entrySet()) {
				flattenTo(e.getKey(), output);
				output.add(":");
				flattenTo(e.getValue(), output);
				output.add(",");
			}
			output.set(output.size() - 1, "...");
			flattenTo(mwt.tail, output);
			output.add("}");
		} else {
			output.add(obj);
		}
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		if (tokens.get(templatePos).equals(")")) {
			int x;
			for (x = templatePos - 1; !tokens.get(x).equals("("); x--) {
				if (tokens.get(x).equals(")"))
					return ParseResult.fail();
			}
			final List<Object> subTokens = tokens.subList(x + 1, templatePos);
			final List<Object> parsedSubTokens = context.parseEngine.parseAndGetLastStep(subTokens, context);

			final List<Object> result = Lists.newArrayListWithCapacity(tokens.size());

			result.addAll(tokens.subList(0, x));
			if (parsedSubTokens.size() == 1) {
				result.add(parsedSubTokens.get(0));
			} else {
				result.add(new SubTokenSequence(parsedSubTokens));
			}
			result.addAll(tokens.subList(templatePos + 1, tokens.size()));

			return ParseResult.success(result, parsedSubTokens.size() - 1);
		} else if (tokens.get(templatePos).equals("]")) {
			int x;
			for (x = templatePos - 1; !tokens.get(x).equals("["); x--) {
				if (tokens.get(x).equals("]"))
					return ParseResult.fail();
			}
			final List<Object> inListTokens = tokens.subList(x + 1, templatePos);

			final List<SubTokenSequence> sequenceList = Lists.newArrayListWithCapacity(tokens.size());

			Object retTail = null;
			boolean nextIsTail = false;

			for (final Object o : inListTokens) {
				if (sequenceList.isEmpty() || o.equals(",")) {
					if (!nextIsTail) {
						sequenceList.add(new SubTokenSequence(Lists.newArrayListWithCapacity(3)));
					} else
						// Can't have commas after ...
						return ParseResult.fail();  // TODO: Should generate parse error
				}
				if (o.equals(",")) {
					continue;
				}
				if (o.equals("...")) {
					nextIsTail = true;
					continue;
				}
				if (!nextIsTail) {
					sequenceList.get(sequenceList.size() - 1).tokens.add(o);
				} else {
					if (retTail != null) // Can't have multiple tokens after ...
						return ParseResult.fail();
					retTail = o;
				}

			}

			final List<Object> retList = Lists.newArrayListWithCapacity(sequenceList.size());
			for (final SubTokenSequence sts : sequenceList) {
				final List<Object> parsed = context.parseEngine.parseAndGetLastStep(sts.tokens, context);
				if (parsed.size() == 1) {
					retList.add(parsed.get(0));
				} else {
					retList.add(new SubTokenSequence(parsed));
				}
			}

			final List<Object> result = Lists.newArrayListWithCapacity(tokens.size());
			result.addAll(tokens.subList(0, x));
			if (retTail == null) {
				result.add(retList);
			} else if (retTail instanceof List) {
				retList.addAll(((List<Object>) retTail));
				result.add(retList);
			} else {
				result.add(new ListWithTail(retList, retTail));
			}
			result.addAll(tokens.subList(templatePos + 1, tokens.size()));
			return ParseResult.success(result, result.size());
		} else if (tokens.get(templatePos).equals("}")) {
			int x;
			for (x = templatePos - 1; !tokens.get(x).equals("{"); x--) {
				if (tokens.get(x).equals("}"))
					return ParseResult.fail();
			}
			final List<Object> inMapTokens = Lists.newArrayList(tokens.subList(x + 1, templatePos));

			final List<SubTokenSequence> sequenceList = Lists.newArrayListWithCapacity(tokens.size());

			Object tail = null;
			boolean nextIsTail = false;

			for (final Object o : inMapTokens) {
				if (sequenceList.isEmpty() || o.equals(",")) {
					if (nextIsTail)
						return ParseResult.fail(); // TODO: Should be parse error
					sequenceList.add(new SubTokenSequence(Lists.newArrayListWithCapacity(3)));
				}
				if (o.equals(",")) {
					continue;
				}
				if (o.equals("...")) {
					nextIsTail = true;
					continue;
				}
				if (nextIsTail) {
					if (tail != null)
						return ParseResult.fail();
					tail = o;
				} else {
					sequenceList.get(sequenceList.size() - 1).tokens.add(o);
				}

			}

			final Map<Object, Object> retMap = Maps.newLinkedHashMap();
			for (final SubTokenSequence sts : sequenceList) {
				final int mps = sts.tokens.indexOf(":");
				if (mps == -1)
					return ParseResult.fail();
				Object key;
				if (mps == 1) {
					key = sts.tokens.get(0);
				} else {
					key = new SubTokenSequence(context.parseEngine.parseAndGetLastStep(
							Lists.newArrayList(sts.tokens.subList(0, mps)),
							context));
				}
				Object value;
				if (mps == sts.tokens.size() - 2) {
					value = sts.tokens.get(sts.tokens.size() - 1);
				} else {
					value = new SubTokenSequence(context.parseEngine.parseAndGetLastStep(
							sts.tokens.subList(mps + 1, sts.tokens.size()), context));
				}
				retMap.put(key, value);
			}

			if (tail != null && tail instanceof Map) {
				retMap.putAll((Map<Object, Object>) tail);
				tail = null;
			}

			final List<Object> result = Lists.newArrayListWithCapacity(tokens.size());
			result.addAll(tokens.subList(0, x));
			if (tail == null) {
				result.add(retMap);
			} else {
				result.add(new MapWithTail(retMap, tail));
			}
			result.addAll(tokens.subList(templatePos + 1, tokens.size()));
			return ParseResult.success(result, result.size());
		}
		return ParseResult.fail();
	}

	@Override
	public String toString() {
		return "";
	}

	public static class SubTokenSequence implements Serializable {
		private static final long serialVersionUID = -8924157711289740438L;

		public List<Object> tokens;

		public SubTokenSequence(final List<Object> tokens) {
			this.tokens = tokens;
		}

		@Override
		public String toString() {
			return "(" + Misc.joiner.join(tokens) + ")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SubTokenSequence))
				return false;
			final SubTokenSequence other = (SubTokenSequence) obj;
			if (tokens == null) {
				if (other.tokens != null)
					return false;
			} else if (!tokens.equals(other.tokens))
				return false;
			return true;
		}

		private void writeObject(final ObjectOutputStream out) throws IOException {
			out.writeInt(tokens.size());
			for (final Object o : tokens) {
				out.writeObject(o);
			}
		}

		private void readObject(final ObjectInputStream in) throws IOException {
			final int size = in.readInt();
			tokens = Lists.newArrayListWithCapacity(size);
			for (int x = 0; x < size; x++) {
				try {
					tokens.add(in.readObject());
				} catch (final ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		return "BracketsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PreParser;
	}

	public static class ListWithTail implements Serializable {
		private static final long serialVersionUID = -529889868305161556L;

		public final List<Object> list;

		public final Object tail;

		public ListWithTail(final List<Object> list, final Object tail) {
			this.list = list;
			this.tail = tail;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("ListWithTail [list=");
			builder.append(list);
			builder.append(", tail=");
			builder.append(tail);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((list == null) ? 0 : list.hashCode());
			result = prime * result + ((tail == null) ? 0 : tail.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof ListWithTail))
				return false;
			final ListWithTail other = (ListWithTail) obj;
			if (list == null) {
				if (other.list != null)
					return false;
			} else if (!list.equals(other.list))
				return false;
			if (tail == null) {
				if (other.tail != null)
					return false;
			} else if (!tail.equals(other.tail))
				return false;
			return true;
		}

	}

	public static class MapWithTail implements Serializable {
		private static final long serialVersionUID = -3556614307132116724L;

		public final Map<Object, Object> map;

		public final Object tail;

		public MapWithTail(final Map<Object, Object> map, final Object tail) {
			this.map = map;
			this.tail = tail;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("MapWithTail [map=");
			builder.append(map);
			builder.append(", tail=");
			builder.append(tail);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			result = prime * result + ((tail == null) ? 0 : tail.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof MapWithTail))
				return false;
			final MapWithTail other = (MapWithTail) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			if (tail == null) {
				if (other.tail != null)
					return false;
			} else if (!tail.equals(other.tail))
				return false;
			return true;
		}

	}
}
