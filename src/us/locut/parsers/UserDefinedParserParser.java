package us.locut.parsers;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.*;

import us.locut.parsers.PreParser.ListWithTail;
import us.locut.parsers.PreParser.MapWithTail;
import us.locut.parsers.PreParser.SubTokenSequence;

public class UserDefinedParserParser extends Parser {

	private static final long serialVersionUID = -6964937711038633291L;
	private static ArrayList<Object> template;

	static {
		template = Lists.newArrayList();
		template.add(Lists.<Object> newArrayList("=", "is", "means"));
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {

		for (final Object o : tokens) {
			if (PreParser.reserved.contains(o))
				return ParseResult.fail();
		}
		final List<Object> before = Lists.newArrayList(tokens.subList(0, templatePos));

		final List<Object> after = PreParser.flatten(tokens.subList(templatePos + 1, tokens.size()));

		final List<Object> response = Lists.newArrayListWithCapacity(tokens.size() + 1 + tokens.size());

		final Set<String> variables = Sets.newHashSet();

		for (final Object o : PreParser.flatten(before)) {
			if (o instanceof String && !PreParser.reserved.contains(o) && after.contains(o)
					&& Character.isUpperCase(((String) o).charAt(0))) {
				variables.add((String) o);
			}
		}

		response.add(new UserDefinedParser(before, after, variables));

		return ParseResult.success(response, -2);
	}

	public static class BindException extends Exception {
		private static final long serialVersionUID = -2021162162489202197L;

		public BindException(final String err) {
			super(err);
		}
	}

	@SuppressWarnings("unchecked")
	protected static void bind(final Object from, final Object to, final Set<String> variables,
			final Map<String, Object> bound)
					throws BindException {
		if (from.equals(to) || from.equals("?"))
			return;
		else if (from instanceof SubTokenSequence) {
			if (to instanceof SubTokenSequence) {
				bind(((SubTokenSequence) from).tokens, ((SubTokenSequence) to).tokens, variables, bound);
			} else if (to instanceof List) {
				bind(((SubTokenSequence) from).tokens, to, variables, bound);
			} else
				throw new BindException("Don't know how to bind " + from + ":" + from.getClass().getSimpleName() + " to "
						+ to + ":" + to.getClass().getSimpleName());
		} else if (from instanceof String && variables.contains(from)) {
			bound.put((String) from, to);
		} else if (from instanceof List && to instanceof List) {
			final List<Object> fromL = (List<Object>) from;
			final List<Object> toL = (List<Object>) to;
			if (fromL.size() != toL.size())
				throw new BindException("Lists are not of same size");
			for (int x = 0; x < fromL.size(); x++) {
				bind(fromL.get(x), toL.get(x), variables, bound);
			}
		} else if (from instanceof MapWithTail && to instanceof Map) {
			final MapWithTail fromMWT = (MapWithTail) from;
			final Map<Object, Object> toM = (Map<Object, Object>) to;
			final Map<Object, Object> tail = Maps.newLinkedHashMap(toM);
			if (toM.size() < fromMWT.map.size())
				throw new BindException("Map must be at least size of head of MWT");
			for (final Entry<Object, Object> e : fromMWT.map.entrySet()) {
				if (!variables.contains(e.getValue()))
					throw new BindException("Map value '"+e.getValue()+" is not a variable");
				if (variables.contains(e.getKey()) && !bound.containsKey(e.getKey())) {
					// The key is a variable, bind to any value and remove from
					// tail
					final Map.Entry<Object, Object> nextEntry = tail.entrySet().iterator().next();
					bound.put((String) e.getKey(), nextEntry.getKey());
					bind(e.getValue(), nextEntry.getValue(), variables, bound);
					tail.remove(nextEntry.getKey());
				} else {
					final Object aKey = bound.containsKey(e.getKey()) ? bound.get(e.getKey()) : e.getKey();
					final Object value = toM.get(aKey);
					if (value == null)
						throw new BindException("Map doesn't contain key '" + e.getKey() + "'");
					bind(e.getValue(), value, variables, bound);
					tail.remove(e.getKey());
				}
			}
			bind(fromMWT.tail, tail, variables, bound);
		} else if (from instanceof ListWithTail && to instanceof List) {
			final ListWithTail fromLWT = (ListWithTail) from;
			final List<Object> toList = (List<Object>) to;
			if (toList.size() < fromLWT.list.size())
				throw new BindException("List must be at least size of head of LWT");
			if (toList.isEmpty())
				throw new BindException("Can't bind list with tail to empty list");
			final LinkedList<Object> tail = Lists.newLinkedList(toList);
			for (int x = 0; x < fromLWT.list.size(); x++) {
				bind(fromLWT.list.get(x), toList.get(x), variables, bound);
				tail.removeFirst();
			}
			bind(fromLWT.tail, tail, variables, bound);
		} else
			throw new BindException("Don't know how to bind " + from + ":" + from.getClass().getSimpleName() + " to "
					+ to + ":" + to.getClass().getSimpleName());
	}

	public static class UserDefinedParser extends Parser {
		private static final long serialVersionUID = -4928516936219533258L;

		public final List<Object> after;

		private final ArrayList<Object> template;

		private final List<Object> before;

		private final Set<String> variables;

		public UserDefinedParser(final List<Object> before, final List<Object> after, final Set<String> variables) {
			this.before = before;
			this.after = after;
			this.variables = variables;
			template = Lists.newArrayList();

			for (final Object o : before) {
				if (variables.contains(o)) {
					template.add(Object.class);
				} else if (o instanceof String) {
					template.add(o);
				} else if (o instanceof MapWithTail || o instanceof Map) {
					template.add(Map.class);
				} else if (o instanceof ListWithTail || o instanceof List) {
					template.add(List.class);
				} else {
					template.add(o.getClass());
				}
			}

		}

		@Override
		public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
			final List<Object> result = Lists.newArrayListWithCapacity(after.size());
			final List<Object> input = tokens.subList(templatePos, templatePos + template.size());
			final Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(variables.size());
			try {
				bind(before, input, variables, varMap);
				for (final Object o : after) {
					final Object val = varMap.get(o);
					if (val != null) {
						result.add(val);
					} else {
						result.add(o);
					}
				}
			} catch (final BindException e) {
				return ParseResult.fail();
			}
			final List<Object> parsedResult = context.parseEngine.parseAndGetLastStep(result, context);
			return ParseResult.success(createResponseWithCollection(tokens, templatePos, parsedResult), -result.size());
		}

		@Override
		public ArrayList<Object> getTemplate() {
			return template;
		}

		@Override
		public String toString() {
			return before + " = " + after;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((after == null) ? 0 : after.hashCode());
			result = prime * result + ((before == null) ? 0 : before.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof UserDefinedParser))
				return false;
			final UserDefinedParser other = (UserDefinedParser) obj;
			if (after == null) {
				if (other.after != null)
					return false;
			} else if (!after.equals(other.after))
				return false;
			if (before == null) {
				if (other.before != null)
					return false;
			} else if (!before.equals(other.before))
				return false;
			return true;
		}
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "UserDefinedParserParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof UserDefinedParserParser;
	}

}
