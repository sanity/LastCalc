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
package com.lastcalc.parsers.amounts;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;


public class UnitStripper extends Parser {
	private static final long serialVersionUID = 5689741343269900107L;
	private static final TokenList template = TokenList.createD(Amount.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Amount<?> amount = (Amount<?>) tokens.get(templatePos);
		if (amount.getUnit().equals(Unit.ONE))
			return ParseResult.fail();
		return ParseResult.success(
				tokens.replaceWithTokens(templatePos, templatePos + template.size(),
						Amount.valueOf(amount.getEstimatedValue(), Unit.ONE)), 2);
	}

	@Override
	public int hashCode() {
		return "UnitStripper".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof UnitStripper;
	}

}
