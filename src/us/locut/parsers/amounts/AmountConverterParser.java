package us.locut.parsers.amounts;

import java.util.*;

import javax.measure.converter.ConversionException;
import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import us.locut.parsers.Parser;

import com.google.common.collect.Lists;

public class AmountConverterParser extends Parser {
	private static final long serialVersionUID = -2549484003198615095L;
	private static final ArrayList<Object> template = Lists.<Object> newArrayList(Amount.class,
			Lists.newArrayList("in", "to", "as"), Unit.class);

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final Amount<?> amount = (Amount<?>) tokens.get(templatePos);
		final Unit<?> unit = (Unit<?>) tokens.get(templatePos + 2);
		try {
			return ParseResult.success(createResponse(tokens, templatePos, amount.to(unit)));
		} catch (final ConversionException e) {
			// return ParseResult.error("Cannot convert " + amount.getUnit() +
			// " to " + unit);
			return ParseResult.fail();
		}
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
