package com.lastcalc.parsers.bool;

import com.google.common.collect.Lists;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class EqualityParser extends Parser {
	private static final long serialVersionUID = -2750832188085003939L;
	private static TokenList template = TokenList.createD(Object.class, Lists.newArrayList("==", "!="), Object.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Object a = tokens.get(templatePos);
		final String op = (String) tokens.get(templatePos + 1);
		final Object b = tokens.get(templatePos + 2);
		final boolean result = op.equals("==") ? a.equals(b) : !a.equals(b);
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "EqualityParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EqualityParser;
	}

}
