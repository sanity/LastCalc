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

import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;

import org.jscience.mathematics.number.FloatingPoint;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Number;

import com.google.common.collect.*;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import com.lastcalc.parsers.Parser.ParseResult;


public class FactorialParser extends Parser {

	private static final long serialVersionUID = 2056242056369944551L;
	
	private static TokenList template = TokenList.createD(Lists.<Object> newArrayList("factorial"),LargeInteger.class);

	//private static TokenList template=TokenList.createD(LargeInteger.class,Lists.<Object> newArrayList("!"));
	
	@Override
	public TokenList getTemplate() {
		return template;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		
		final String op = (String) tokens.get(templatePos);
		final LargeInteger input = (LargeInteger) tokens.get(templatePos + 1);
		Number result = null;
		
		if(op.equals("factorial")){
			//System.out.println("INput is: "+input);
			
			if(input.isLessThan(LargeInteger.valueOf(20)) && input.isGreaterThan(LargeInteger.ZERO)){
				result = LargeInteger.valueOf(factorial(input.longValue()));
				
				return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos+template.size(), result));
			}
			
			
		}
		return ParseResult.fail();
	}
	
	protected long factorial(long input){
		long fact = 1;
		for (long x = input; x > 1; x--){
			fact *= x;
		}
	 return fact;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public int hashCode() {
		return "FactorialParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return false;
	}
}
