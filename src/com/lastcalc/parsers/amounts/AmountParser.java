package com.lastcalc.parsers.amounts;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;


public class AmountParser extends Parser {
	private static final long serialVersionUID = 9120544485351922021L;
	private static final TokenList template = TokenList.createD(Number.class, Unit.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Number number = (Number) tokens.get(templatePos);
		final Unit<?> unit = (Unit<?>) tokens.get(templatePos + 1);
		if (number.longValue() == number.doubleValue())
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
					Amount.valueOf(number.longValue(), unit)));
		else
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
					Amount.valueOf(number.doubleValue(), unit)));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "AmountParser".hashCode();
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AmountParser;
	}
}
