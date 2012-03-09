/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
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
