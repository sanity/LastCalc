package com.lastcalc.parsers;

import com.lastcalc.TokenList;

public class NoopParser extends Parser {
	private static final long serialVersionUID = 7600767637692555261L;
	public static final NoopParser singleton = new NoopParser();

	@Override
	public TokenList getTemplate() {
		return null;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return ParseResult.success(tokens);
	}

	@Override
	public int hashCode() {
		return "NoopParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof NoopParser;
	}

}
