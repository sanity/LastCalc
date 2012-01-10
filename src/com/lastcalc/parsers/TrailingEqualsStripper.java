package com.lastcalc.parsers;

import com.lastcalc.TokenList;

public class TrailingEqualsStripper extends Parser {
	private static final TokenList template = TokenList.createD("=");
	private static final long serialVersionUID = 2340131974673871340L;

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "TrailingEqualsStripper".hashCode();
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		if (templatePos == tokens.size()-1)
			return ParseResult.success(tokens.subList(0, tokens.size() - 1));
		else
			return ParseResult.fail();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TrailingEqualsStripper;
	}

}
