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
package com.lastcalc.engines;

import java.util.LinkedList;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.ParserContext;


public abstract class ParseEngine {

	public TokenList parseAndGetLastStep(final TokenList input,
 final ParserContext context,
			final TokenList... alternateInputs) {
		final LinkedList<ParseStep> parse = parse(input, context, alternateInputs);
		if (parse.isEmpty())
			throw new RuntimeException("Parse resulted in no ParseSteps");
		return parse.getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final TokenList input,
			ParserContext context,
			TokenList... alternateInputs);

}
