package com.lastcalc.parsers.bool;

import com.google.common.collect.Lists;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class BoolFunctionsParser extends Parser {
	private static final long serialVersionUID = -7820633700412274072L;
	private static TokenList template = TokenList.createD(Lists.newArrayList("not", "!"),
			Boolean.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final boolean a = (Boolean) tokens.get(templatePos + 1);
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), !a));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "BoolFunctionsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BoolFunctionsParser;
	}

}
