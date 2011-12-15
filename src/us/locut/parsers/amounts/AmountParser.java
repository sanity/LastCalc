package us.locut.parsers.amounts;

import java.util.ArrayList;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import us.locut.parsers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class AmountParser extends Parser {

	private static final ArrayList<Object> template = Lists.<Object> newArrayList(Number.class, Unit.class);

	@Override
	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
		final Number number = (Number) tokens.get(templatePos);
		final Unit<?> unit = (Unit<?>) tokens.get(templatePos + 1);
		if (number.longValue() == number.doubleValue())
			return ParseResult.success(createResponse(tokens, templatePos, Amount.valueOf(number.longValue(), unit)),
					null);
		else
			return ParseResult.success(createResponse(tokens, templatePos, Amount.valueOf(number.doubleValue(), unit)),
					null);
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
