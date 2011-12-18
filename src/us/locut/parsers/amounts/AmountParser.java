package us.locut.parsers.amounts;

import java.util.*;

import javax.measure.unit.Unit;

import com.google.common.collect.Lists;

import org.jscience.physics.amount.Amount;

import us.locut.parsers.Parser;

public class AmountParser extends Parser {
	private static final long serialVersionUID = 9120544485351922021L;
	private static final ArrayList<Object> template = Lists.<Object> newArrayList(Number.class, Unit.class);

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final Number number = (Number) tokens.get(templatePos);
		final Unit<?> unit = (Unit<?>) tokens.get(templatePos + 1);
		if (number.longValue() == number.doubleValue())
			return ParseResult.success(createResponse(tokens, templatePos, Amount.valueOf(number.longValue(), unit)));
		else
			return ParseResult.success(createResponse(tokens, templatePos, Amount.valueOf(number.doubleValue(), unit)));
	}

	@Override
	public ArrayList<Object> getTemplate() {
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
