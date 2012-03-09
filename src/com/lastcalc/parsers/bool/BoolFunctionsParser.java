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

import com.google.common.collect.Lists;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class BoolFunctionsParser extends Parser {
	private static final long serialVersionUID = -7820633700412274072L;
	private static TokenList template = TokenList.createD(Lists.newArrayList("not", "!"),
			Boolean.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final boolean a = (Boolean) tokens.get(templatePos + 1);
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), !a));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "BoolFunctionsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BoolFunctionsParser;
	}

}
