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

public class NoopParser extends Parser {
	private static final long serialVersionUID = 7600767637692555261L;
	public static final NoopParser singleton = new NoopParser();

	@Override
	public TokenList getTemplate() {
		return null;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return ParseResult.success(tokens);
	}

	@Override
	public int hashCode() {
		return "NoopParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof NoopParser;
	}

}
