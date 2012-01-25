package com.lastcalc.parsers.currency;

import org.jscience.economics.money.Currency;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class CurrencyReverser extends Parser {

	public static TokenList template = TokenList.createD(Currency.class, Number.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
				tokens.get(templatePos + 1), tokens.get(templatePos)), -1);
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "CurrencyReverser".hashCode();
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof CurrencyReverser;
	}

}
