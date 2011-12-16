package us.locut.parsers;

import java.util.ArrayList;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class UserDefinedParserParser extends Parser {
	private static final long serialVersionUID = -6964937711038633291L;
	private static ArrayList<Object> template;

	static {
		template = Lists.newArrayList();
		template.add(Lists.<Object> newArrayList("=", "is", "means"));
	}

	@Override
	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos, final ParserContext context) {
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
		end--;
		return null;
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
