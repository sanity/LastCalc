package us.locut.parsers;

import java.io.Serializable;
import java.util.*;

import com.google.common.collect.*;

import us.locut.Misc;

public class PreParser extends Parser {
	private static final long serialVersionUID = 3705611710430408505L;

	private static ArrayList<Object> template;

	public static Set<String> reserved = Sets.newHashSet("(", ")", "{", "}", "[", "]", ",", ":");

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
		}
		if (!willFlatten)
			return input;

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
			// Overwrite last ","
			output.set(output.size() - 1, "]");
		} else if (obj instanceof Map) {
			final Map<Object, Object> map = (Map<Object, Object>) obj;
			output.add("{");
			for (final Map.Entry<Object, Object> e : map.entrySet()) {
				flattenTo(e.getKey(), output);
				output.add(":");
				flattenTo(e.getValue(), output);
				output.add(",");
			}
			output.set(output.size() - 1, "}");

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

			for (final Object o : inListTokens) {
				if (sequenceList.isEmpty() || o.equals(",")) {
					sequenceList.add(new SubTokenSequence(Lists.newArrayListWithCapacity(3)));
				}
				if (o.equals(",")) {
					continue;
				}
				sequenceList.get(sequenceList.size() - 1).tokens.add(o);

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
			result.add(retList);
			result.addAll(tokens.subList(templatePos + 1, tokens.size()));
			return ParseResult.success(result, result.size());
		} else if (tokens.get(templatePos).equals("}")) {
			int x;
			for (x = templatePos - 1; !tokens.get(x).equals("{"); x--) {
				if (tokens.get(x).equals("}"))
					return ParseResult.fail();
			}
			final List<Object> inMapTokens = tokens.subList(x + 1, templatePos);

			final List<SubTokenSequence> sequenceList = Lists.newArrayListWithCapacity(tokens.size());

			for (final Object o : inMapTokens) {
				if (sequenceList.isEmpty() || o.equals(",")) {
					sequenceList.add(new SubTokenSequence(Lists.newArrayListWithCapacity(3)));
				}
				if (o.equals(",")) {
					continue;
				}
				sequenceList.get(sequenceList.size() - 1).tokens.add(o);

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
					key = new SubTokenSequence(context.parseEngine.parseAndGetLastStep(sts.tokens.subList(0, mps),
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

			final List<Object> result = Lists.newArrayListWithCapacity(tokens.size());
			result.addAll(tokens.subList(0, x));
			result.add(retMap);
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

		public final List<Object> tokens;

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

	}

	@Override
	public int hashCode() {
		return "BracketsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PreParser;
	}

}
