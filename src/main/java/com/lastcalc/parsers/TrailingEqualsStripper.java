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

public class TrailingEqualsStripper extends Parser {
	private static final TokenList template = TokenList.createD("=");
	private static final long serialVersionUID = 2340131974673871340L;

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "TrailingEqualsStripper".hashCode();
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		if (templatePos == tokens.size()-1)
			return ParseResult.success(tokens.subList(0, tokens.size() - 1));
		else
			return ParseResult.fail();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TrailingEqualsStripper;
	}

}
