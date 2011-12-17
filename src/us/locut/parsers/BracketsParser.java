package us.locut.parsers;

import java.util.*;

import com.google.common.collect.Lists;

public class BracketsParser extends Parser {
	private static final long serialVersionUID = 3705611710430408505L;

	@Override
	public ArrayList<Object> getTemplate() {
		return Lists.<Object> newArrayList("(", Object.class, ")");
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		return ParseResult.success(createResponse(tokens, templatePos, tokens.get(templatePos + 1)));
	}

	@Override
	public int hashCode() {
		return "BracketsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BracketsParser;
	}

}
