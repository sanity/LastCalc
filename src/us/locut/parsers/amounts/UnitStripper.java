package us.locut.parsers.amounts;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import us.locut.TokenList;
import us.locut.parsers.Parser;

public class UnitStripper extends Parser {
	private static final long serialVersionUID = 5689741343269900107L;
	private static final TokenList template = TokenList.createD(Amount.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Amount<?> amount = (Amount<?>) tokens.get(templatePos);
		if (amount.getUnit().equals(Unit.ONE))
			return ParseResult.fail();
		return ParseResult.success(
				tokens.replaceWithTokens(templatePos, templatePos + template.size(),
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
