package us.locut.parsers.amounts;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import us.locut.TokenList;
import us.locut.parsers.Parser;

public class DimensionlessAmountParser extends Parser {
	private static final long serialVersionUID = 5612612660090224236L;
	private static final TokenList template = TokenList.createD(Number.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Number number = (Number) tokens.get(templatePos);
		if (number.longValue() == number.doubleValue())
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
					Amount.valueOf(number.longValue(), Unit.ONE)));
		else
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
					Amount.valueOf(number.doubleValue(), Unit.ONE)));
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public TokenList getTemplate() {
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
