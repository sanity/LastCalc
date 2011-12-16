package us.locut.parsers;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import com.google.appengine.repackaged.com.google.common.collect.*;

public class UserDefinedParserParser extends Parser {
	private static final long serialVersionUID = -6964937711038633291L;
	private static ArrayList<Object> template;

	static {
		template = Lists.newArrayList();
		template.add(Lists.<Object> newArrayList("=", "is", "means"));
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		// Walk backwards to find start
		int depth = 0, start;
		for (start = templatePos; start > -1; start--) {
			if (tokens.get(start).equals(")")) {
				depth++;
			} else if (tokens.get(start).equals("(")) {
				depth--;
			}
			if (depth < 0) {
				break;
			}
		}
		start++;
		depth = 0;
		int end;
		for (end = templatePos; end < tokens.size(); end++) {
			if (tokens.get(end).equals("(")) {
				depth++;
			} else if (tokens.get(end).equals(")")) {
				depth--;
			}
			if (depth < 0) {
				break;
			}
		}

		final List<Object> before = tokens.subList(start, templatePos);

		final List<Object> after = tokens.subList(templatePos + 1, end);

		final List<Object> response = Lists.newArrayListWithCapacity(tokens.size() + 1 + (end - start));

		response.addAll(tokens.subList(0, start - 1));
		response.add(new UserDefinedParser(before, after));
		response.addAll(tokens.subList(end + 1, tokens.size()));

		return ParseResult.success(response);
	}

	public static class UserDefinedParser extends Parser {
		private static final long serialVersionUID = -4928516936219533258L;

		public final Map<Integer, Variable> variables = Maps.newHashMap();

		public final List<Object> after;

		public static class Variable implements Serializable {
			private static final long serialVersionUID = -378043931861794454L;
			public String name;
			public Set<Integer> replacePositions;

			public Variable(final String name, final Set<Integer> replacePositions) {
				this.name = name;
				this.replacePositions = replacePositions;
			}
		}

		public UserDefinedParser(final List<Object> before, final List<Object> after) {
			this.after = after;
			template = Lists.newArrayListWithCapacity(before.size());
			for (int x = 0; x < before.size(); x++) {
				final Object s = before.get(x);
				if (s instanceof String) {
					final Set<Integer> positions = Sets.newHashSet();
					for (int y = 0; y < after.size(); y++) {
						if (s.equals(after.get(y))) {
							positions.add(y);
						}
					}
					if (!positions.isEmpty()) {
						variables.put(x, new Variable(s.toString(), positions));
						template.add(Object.class);
						continue;
					}
				}
				template.add(s);
			}
		}

		@Override
		public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
			final List<Object> result = Lists.newArrayListWithCapacity(after.size());
			result.addAll(after);
			for (final Entry<Integer, Variable> varE : variables.entrySet()) {
				final Object val = tokens.get(varE.getKey());
				for (final int pos : varE.getValue().replacePositions) {
					result.set(pos, val);
				}
			}
			return ParseResult.success(createResponseWithCollection(tokens, templatePos, result));
		}

		@Override
		public ArrayList<Object> getTemplate() {
			return template;
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean equals(final Object obj) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
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
