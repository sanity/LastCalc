package com.lastcalc.parsers.bool;

import com.google.common.collect.Lists;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class NotParser extends Parser {
	private static final long serialVersionUID = -7820633700412274072L;
	private static TokenList template = TokenList.createD(Boolean.class, Lists.newArrayList("and", "or", "xor"),
			Boolean.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final boolean a = (Boolean) tokens.get(templatePos);
		final boolean b = (Boolean) tokens.get(templatePos + 2);
		final String function = (String) tokens.get(templatePos + 1);
		boolean ret;
		if (function.equals("and")) {
			ret = a && b;
		} else if (function.equals("or")) {
			ret = a || b;
		} else {
			ret = a != b;
		}
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), ret));
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
		return obj instanceof NotParser;
	}

}
