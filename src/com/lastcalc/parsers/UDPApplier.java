package com.lastcalc.parsers;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

public class UDPApplier extends Parser {
	private static final long serialVersionUID = -5412238263419670848L;
	private static TokenList template = TokenList.createD("apply", UserDefinedParser.class, "to");

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		final UserDefinedParser function = (UserDefinedParser) tokens.get(templatePos + 1);
		if (2 + templatePos + function.getTemplate().size() > tokens.size())
			return ParseResult.fail();
		final TokenList input = tokens.subList(templatePos + 3, templatePos + 3 + function.getTemplate().size());
		if (function.matchTemplate(input) != 0)
			return ParseResult.fail();
		final ParseResult parse = function.parse(input, 0, context);
		if (parse.isSuccess())
			return ParseResult.success(tokens.replaceWithTokenList(templatePos, templatePos + 3
					+ function.getTemplate().size(), parse.output));
		else
			return ParseResult.fail();
	}

	@Override
	public int hashCode() {
		return "UDPApplier".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof UDPApplier;
	}

}
