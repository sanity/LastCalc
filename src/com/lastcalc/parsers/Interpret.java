package com.lastcalc.parsers;

import com.lastcalc.*;
import com.lastcalc.Tokenizer.QuotedString;

public class Interpret extends Parser {
	private static final long serialVersionUID = 4507416694168613210L;

	private static TokenList template = TokenList.createD("interpret", Tokenizer.QuotedString.class);

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
		final Tokenizer.QuotedString string = (QuotedString) tokens.get(templatePos + 1);
		final TokenList ret = Tokenizer.tokenize(string.value);
		return ParseResult.success(tokens.replaceWithTokenList(templatePos, templatePos + template.size(), ret),
				-ret.size());
	}

	@Override
	public int hashCode() {
		return "ToLowerCase".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Interpret;
	}

}
