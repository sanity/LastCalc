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
package com.lastcalc.parsers.math;

import com.google.common.collect.Lists;

import org.jscience.mathematics.number.LargeInteger;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;


public class RadixConverter extends Parser {
	private static final long serialVersionUID = 9120544485351922021L;
	private static final TokenList template = TokenList.createD(Lists.newArrayList(LargeInteger.class, Radix.class),
			Lists.newArrayList("to", "as", "in"),
			Lists.newArrayList("bin", "binary", "hex", "hexadecimal", "oct", "octal", "dec", "decimal"));

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Object num = tokens.get(templatePos);
		final long li = num instanceof LargeInteger ?((LargeInteger) tokens.get(templatePos)).longValue(): ((Radix) num).integer;

		final String convertTo = (String) tokens.get(templatePos + 2);
		Object result;
		if (convertTo.startsWith("bin")) {
			result = new Radix(li, 2);
		} else if (convertTo.startsWith("hex")) {
			result = new Radix(li, 16);
		} else if (convertTo.startsWith("oct")) {
			result = new Radix(li, 8);
		} else {
			result = LargeInteger.valueOf(li);
		}
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "RadixConverter".hashCode();
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof RadixConverter;
	}
}
