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
package com.lastcalc.parsers.currency;

import org.jscience.economics.money.Currency;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class CurrencyReverser extends Parser {

	public static TokenList template = TokenList.createD(Currency.class, Number.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
				tokens.get(templatePos + 1), tokens.get(templatePos)), -1);
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "CurrencyReverser".hashCode();
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof CurrencyReverser;
	}

}
