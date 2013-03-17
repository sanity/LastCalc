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
package com.lastcalc.parsers.bool;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;

public class IfThenElse extends Parser {
	private static final long serialVersionUID = -7731508022221902181L;

	private static TokenList template = TokenList.createD("if", Boolean.class, "then");

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		final boolean condition = (Boolean) tokens.get(templatePos + 1);
		final int end = PreParser.findEdgeOrObjectForwards(tokens, templatePos + 2, null) + 1;
		final int elsePos = PreParser.findEdgeOrObjectForwards(tokens, templatePos + 2, "else");
		if (!tokens.get(elsePos).equals("else"))
			return ParseResult.fail();
		TokenList ret;
		if (condition) {
			ret = new TokenList.SubTokenList(tokens, templatePos + 3, elsePos);
		} else {
			ret = new TokenList.SubTokenList(tokens, elsePos + 1, end);
		}
		return ParseResult.success(tokens.replaceWithTokenList(templatePos, end, ret));
	}

	@Override
	public int hashCode() {
		return "IfThenElse".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IfThenElse;
	}

}
