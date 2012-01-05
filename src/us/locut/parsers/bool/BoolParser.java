package us.locut.parsers.bool;

import java.util.*;

import com.google.common.collect.Lists;

import us.locut.parsers.Parser;

public class BoolParser extends Parser {
	private static final long serialVersionUID = -7666856639055196045L;
	private static ArrayList<Object> template;

	static {
		template = Lists.newArrayList();
		template.add(Lists.newArrayList("true", "yes", "false", "no"));
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final boolean result = (tokens.get(templatePos).equals("true") || tokens.get(templatePos).equals("yes"));
		return ParseResult.success(createResponse(tokens, templatePos, result), -1);
	}

	@Override
	public int hashCode() {
		return "BoolParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BoolParser;
	}

}
