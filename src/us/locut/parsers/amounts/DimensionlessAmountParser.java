package us.locut.parsers.amounts;

import java.util.*;

import javax.measure.unit.Unit;

import com.google.common.collect.Lists;

import org.jscience.physics.amount.Amount;

import us.locut.parsers.Parser;

public class DimensionlessAmountParser extends Parser {
	private static final long serialVersionUID = 5612612660090224236L;
	private static final ArrayList<Object> template = Lists.<Object> newArrayList(Number.class);

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final Number number = (Number) tokens.get(templatePos);
		if (number.longValue() == number.doubleValue())
			return ParseResult.success(
createResponse(tokens, templatePos, Amount.valueOf(number.longValue(), Unit.ONE)));
		else
			return ParseResult.success(
createResponse(tokens, templatePos,
					Amount.valueOf(number.doubleValue(), Unit.ONE)));
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "DimensionlessAmountParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof DimensionlessAmountParser;
	}
}
