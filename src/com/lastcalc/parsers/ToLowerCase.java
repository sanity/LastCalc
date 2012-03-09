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

public class ToLowerCase extends Parser {
	private static final long serialVersionUID = 4507416694168613210L;

	private static TokenList template = TokenList.createD(String.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final String orig = (String) tokens.get(templatePos);
		final String lowerCase = orig.toLowerCase();
		if (!lowerCase.equals(orig))
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), lowerCase),
					0.5);
		else
			return ParseResult.fail();
	}

	@Override
	public int hashCode() {
		return "ToLowerCase".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ToLowerCase;
	}

}
