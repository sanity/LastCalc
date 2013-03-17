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

import org.jscience.mathematics.number.*;
import org.jscience.mathematics.number.Number;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class MathOp extends Parser {
	private static final long serialVersionUID = 4507416694168613210L;

	private static TokenList template = TokenList.createD(
			Lists.<Object> newArrayList("sqrt", "abs", "cos", "acos",
					"asin", "atan", "cbrt", "cosh", "exp", "cosh", "log", "ln", "round", "sin", "sinh", "tan", "tanh", "-", "factorial"),
					Number.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public String toString() {
		return "";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final String op = (String) tokens.get(templatePos);
		final Number number = (Number) tokens.get(templatePos + 1);
		Number result = null;
		if (op.equals("-") && (templatePos == 0 || !(tokens.get(templatePos-1) instanceof Number))) {
			result = (Number) number.opposite();
		} else if (op.equals("sqrt")) {
			result = FloatingPoint.valueOf(Math.sqrt(number.doubleValue()));
		} else if (op.equals("abs")) {
			if (number instanceof FloatingPoint) {
				result = FloatingPoint.valueOf(Math.abs(number.doubleValue()));
			} else if (number instanceof Rational) {
				result = ((Rational) number).abs();
			} else if (number instanceof LargeInteger) {
				result = ((LargeInteger) number).abs();
			}
		} else if (op.equals("acos")) {
			result = FloatingPoint.valueOf(Math.acos(number.doubleValue()));
		} else if (op.equals("asin")) {
			result = FloatingPoint.valueOf(Math.asin(number.doubleValue()));
		} else if (op.equals("atan")) {
			result = FloatingPoint.valueOf(Math.atan(number.doubleValue()));
		} else if (op.equals("acos")) {
			result = FloatingPoint.valueOf(Math.acos(number.doubleValue()));
		} else if (op.equals("cbrt")) {
			result = FloatingPoint.valueOf(Math.cbrt(number.doubleValue()));
		} else if (op.equals("cos")) {
			result = FloatingPoint.valueOf(Math.cos(number.doubleValue()));
		} else if (op.equals("cosh")) {
			result = FloatingPoint.valueOf(Math.cosh(number.doubleValue()));
		} else if (op.equals("exp")) {
			result = FloatingPoint.valueOf(Math.exp(number.doubleValue()));
		} else if (op.equals("cosh")) {
			result = FloatingPoint.valueOf(Math.cosh(number.doubleValue()));
		} else if (op.equals("ln")) {
			result = FloatingPoint.valueOf(Math.log(number.doubleValue()));
		} else if (op.equals("log")) {
			result = FloatingPoint.valueOf(Math.log10(number.doubleValue()));
		} else if (op.equals("round")) {
			result = FloatingPoint.valueOf(Math.round(number.doubleValue()));
		} else if (op.equals("sin")) {
			result = FloatingPoint.valueOf(Math.sin(number.doubleValue()));
		} else if (op.equals("sinh")) {
			result = FloatingPoint.valueOf(Math.sinh(number.doubleValue()));
		} else if (op.equals("sqrt")) {
			result = FloatingPoint.valueOf(Math.sqrt(number.doubleValue()));
		} else if (op.equals("tan")) {
			result = FloatingPoint.valueOf(Math.tan(number.doubleValue()));
		} else if (op.equals("tanh")) {
			result = FloatingPoint.valueOf(Math.tanh(number.doubleValue()));
		} else if (op.equals("factorial")){
			//to do: check to make sure its an int first
			//number.doublevale==number.longvalue
			int num=number.intValue();
			if(num>=0)	{
				//result = Integer.valueOf(factorial(num));
			}
		}
		
		if (result != null)
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos+template.size(), result));
		else
			return ParseResult.fail();
	}
	
	/*private int factorial(double n) {
		if(n == 0) {
            return 1;
        }
        else {
            return n * factorial(n - 1);
        }
	}*/

	@Override
	public int hashCode() {
		return "MathOp".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof MathOp;
	}

}
