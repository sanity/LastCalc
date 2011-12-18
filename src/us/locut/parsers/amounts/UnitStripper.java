package us.locut.parsers.amounts;

import java.util.*;

import javax.measure.unit.Unit;

import com.google.common.collect.Lists;

import org.jscience.physics.amount.Amount;

import us.locut.parsers.Parser;

public class UnitStripper extends Parser {
	private static final long serialVersionUID = 5689741343269900107L;
	private static final ArrayList<Object> template = Lists.<Object> newArrayList(Amount.class);

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final Amount<?> amount = (Amount<?>) tokens.get(templatePos);
		if (amount.getUnit().equals(Unit.ONE))
			return ParseResult.fail();
		return ParseResult.success(createResponse(tokens, templatePos,
 Amount.valueOf(amount.getEstimatedValue(), Unit.ONE)), 1);
	}

	@Override
	public int hashCode() {
		return "UnitStripper".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof UnitStripper;
	}

}
