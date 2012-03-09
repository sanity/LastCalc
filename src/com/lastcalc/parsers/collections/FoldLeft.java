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
package com.lastcalc.parsers.collections;

import java.util.List;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

public class FoldLeft extends Parser {
	private static final long serialVersionUID = 9178891850811247754L;
	private static TokenList template = TokenList.createD("fold", UserDefinedParser.class, "over", List.class, "with",
			Object.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		Object ret = tokens.get(templatePos + 5);
		final UserDefinedParser udp = (UserDefinedParser) tokens.get(templatePos + 1);
		final Object datastructure = tokens.get(templatePos + 3);
		if (datastructure instanceof List) {
			if (udp.getTemplate().size() != 2)
				return ParseResult.fail();
			final List<Object> list = (List<Object>) datastructure;
			for (final Object o : list) {
				TokenList r = udp.parse(TokenList.createD(ret, o), 0).output;
				r = context.parseEngine.parseAndGetLastStep(r, context);
				if (r.size() != 1)
					return ParseResult.fail();
				else {
					ret = r.get(0);
				}
			}
		} else
			return ParseResult.fail();
		return ParseResult.success(tokens.replaceWithTokenList(templatePos, templatePos + template.size(),
				TokenList.createD(ret)));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "FoldLeft".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof FoldLeft;
	}

}
