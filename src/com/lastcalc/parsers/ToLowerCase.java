package com.lastcalc.parsers;

import com.lastcalc.TokenList;

public class ToLowerCase extends Parser {
	private static final long serialVersionUID = 4507416694168613210L;

	private static TokenList template = TokenList.createD(String.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final String orig = (String) tokens.get(templatePos);
		final String lowerCase = orig.toLowerCase();
		if (!lowerCase.equals(orig))
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), lowerCase),
					0.5);
		else
			return ParseResult.fail();
	}

	@Override
	public int hashCode() {
		return "ToLowerCase".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ToLowerCase;
	}

}
