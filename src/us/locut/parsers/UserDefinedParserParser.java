package us.locut.parsers;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.*;

import us.locut.Misc;

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

		final List<Object> before = Lists.newArrayList(tokens.subList(start, templatePos));

		// Ensure that we only have Strings before the = or "is"
		for (final Object b : before) {
			if (!(b instanceof String))
				return ParseResult.fail();
		}

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

		final List<Object> after = Lists.newArrayList(tokens.subList(templatePos + 1, end));

		final List<Object> response = Lists.newArrayListWithCapacity(tokens.size() + 1 + (end - start));

		response.addAll(tokens.subList(0, Math.max(0, start - 1)));
		response.add(new UserDefinedParser(before, after));
		response.addAll(tokens.subList(Math.min(end + 1, tokens.size()), tokens.size()));

		return ParseResult.success(response);
	}

	public static class UserDefinedParser extends Parser {
		private static final long serialVersionUID = -4928516936219533258L;

		public final Map<Integer, Variable> variables = Maps.newHashMap();

		public final List<Object> after;

		private final ArrayList<Object> template;

		public static class Variable implements Serializable {
			private static final long serialVersionUID = -378043931861794454L;
			public String name;
			public Set<Integer> replacePositions;

			public Variable(final String name, final Set<Integer> replacePositions) {
				this.name = name;
				this.replacePositions = replacePositions;
			}

			@Override
			public String toString() {
				final StringBuilder builder = new StringBuilder();
				builder.append("Variable [name=");
				builder.append(name);
				builder.append(", replacePositions=");
				builder.append(replacePositions);
				builder.append("]");
				return builder.toString();
			}
		}

		public UserDefinedParser(final List<Object> before, final List<Object> after) {
			this.after = after;
			template = Lists.newArrayListWithCapacity(before.size());
			for (int x = 0; x < before.size(); x++) {
				final Object s = before.get(x);
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
				template.add(s);
			}
		}

		@Override
		public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
			final List<Object> result = Lists.newArrayListWithCapacity(after.size());
			result.addAll(after);
			for (final Entry<Integer, Variable> varE : variables.entrySet()) {
				final Object val = tokens.get(templatePos + varE.getKey());
				for (final int pos : varE.getValue().replacePositions) {
					result.set(pos, val);
				}
			}

			return ParseResult.success(createResponseWithCollection(tokens, templatePos, result));
		}

		@Override
		public double getScoreBias() {
			// We don't want to get punished if the result is longer than the
			// input, so counteract this with a bias
			if (after.size() > template.size())
				return template.size() - after.size();
			else
				return 0;
		}

		@Override
		public ArrayList<Object> getTemplate() {
			return template;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder(100);
			sb.append('(');
			final List<Object> b4 = Lists.newArrayListWithCapacity(template.size());
			b4.addAll(template);
			final List<Object> af = Lists.newArrayListWithCapacity(after.size());
			af.addAll(after);
			for (final Entry<Integer, Variable> var : variables.entrySet()) {
				b4.set(var.getKey(), var.getValue().name);
				for (final int a : var.getValue().replacePositions) {
					af.set(a, var.getValue().name);
				}
			}
			sb.append(Misc.joiner.join(b4));

			sb.append(" = ");

			sb.append(Misc.joiner.join(af));

			sb.append(')');
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((after == null) ? 0 : after.hashCode());
			result = prime * result + ((variables == null) ? 0 : variables.hashCode());
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
			if (variables == null) {
				if (other.variables != null)
					return false;
			} else if (!variables.equals(other.variables))
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
