package com.lastcalc.parsers.amounts;

import javax.measure.converter.ConversionException;
import javax.measure.unit.Unit;

import com.google.common.collect.Lists;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.physics.amount.Amount;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;


public class AmountConverterParser extends Parser {
	private static final long serialVersionUID = -2549484003198615095L;
	private static final TokenList template = TokenList.createD(Amount.class,
			Lists.newArrayList("in", "to", "as"),
			Object.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Amount<?> amount = (Amount<?>) tokens.get(templatePos);
		if (tokens.get(templatePos + 2) instanceof Unit) {
			final Unit<?> unit = (Unit<?>) tokens.get(templatePos + 2);
			try {
				return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
						amount.to(unit)));
			} catch (final ConversionException e) {
				// return ParseResult.error("Cannot convert " + amount.getUnit() +
				// " to " + unit);
				return ParseResult.fail();
			}
		} else if (tokens.get(templatePos + 2).equals("number"))
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
					FloatingPoint.valueOf(amount.getEstimatedValue())));
		else
			return ParseResult.fail();
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public int hashCode() {
		return "AmountConverterParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AmountConverterParser;
	}

}
