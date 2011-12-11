package us.locut.reducers.amounts;

import java.util.ArrayList;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import us.locut.reducers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class AmountParser extends Parser {

	private static final ArrayList<Object> template = Lists.<Object> newArrayList(Number.class, Unit.class);

	@Override
	public ParseResult reduce(final ArrayList<Object> tokens, final int templatePos) {
		final Number number = (Number) tokens.get(templatePos);
		final Unit<?> unit = (Unit<?>) tokens.get(templatePos + 1);
		if (number.longValue() == number.doubleValue())
			return new ParseResult(createResponse(tokens, templatePos, Amount.valueOf(number.longValue(), unit)), null);
		else
			return new ParseResult(createResponse(tokens, templatePos, Amount.valueOf(number.doubleValue(), unit)),
					null);
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

}
