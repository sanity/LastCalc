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

import java.util.*;

import javax.measure.unit.Unit;

import com.google.common.collect.*;

import org.jscience.mathematics.number.*;
import org.jscience.physics.amount.Amount;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;

public class MathBiOp extends Parser {
	private static final long serialVersionUID = 4507416694168613210L;

	private static final ArrayList<String> opsRequiringSameUnit = Lists.newArrayList("+", "-", "<", ">", "<=", ">=",
			"==", "!=");

	private static Map<String, Integer> precidenceMap = Maps.newHashMap();
	static {
		precidenceMap.put("^", 2);
		precidenceMap.put("xor", 2);
		precidenceMap.put("*", 3);
		precidenceMap.put("/", 3);
		precidenceMap.put("mod", 3);
		precidenceMap.put("+", 4);
		precidenceMap.put("-", 4);
		precidenceMap.put("<", 6);
		precidenceMap.put("<=", 6);
		precidenceMap.put(">", 6);
		precidenceMap.put(">=", 6);
		precidenceMap.put("==", 7);
		precidenceMap.put("!=", 7);
	}

	private static TokenList template = TokenList.createD(
			Object.class,
			Lists.<Object> newArrayList(precidenceMap.keySet()),
			Object.class);

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
		Object a = tokens.get(templatePos);
		Object b = tokens.get(templatePos + 2);
		int outputRadix = 10;
		if (a instanceof Radix && b instanceof Radix) {
			final Radix ra = (Radix) a;
			final Radix rb = (Radix) b;
			if (ra.radix == rb.radix) {
				outputRadix = ra.radix;
			}
		}
		if (a instanceof Radix) {
			a = LargeInteger.valueOf(((Radix) a).integer);
		}
		if (b instanceof Radix) {
			b = LargeInteger.valueOf(((Radix) b).integer);
		}
		if ((!(a instanceof org.jscience.mathematics.number.Number) && !(a instanceof Amount))
				|| (!(b instanceof org.jscience.mathematics.number.Number) && !(b instanceof Amount)))
			return ParseResult.fail();
		final String op = (String) tokens.get(templatePos + 1);

		final Integer opPrecidence = precidenceMap.get(op);
		boolean samePrecidenceOk = false;
		for (final Object token : PreParser.enclosedByStructure(tokens, templatePos)) {
			if (token == op) {
				// Operators of the same precedence are permitted *after* this
				// operator
				samePrecidenceOk = true;
				continue;
			}
			final Integer tPrecidence = precidenceMap.get(token);
			if (samePrecidenceOk) {
				if (tPrecidence != null && tPrecidence < opPrecidence)
					return Parser.ParseResult.fail();
			} else if (tPrecidence != null && tPrecidence <= opPrecidence)
				return Parser.ParseResult.fail();
		}

		Object result;
		if (a instanceof Amount || b instanceof Amount) {
			final boolean opSameUnit = opsRequiringSameUnit.contains(op);
			if (a instanceof org.jscience.mathematics.number.Number) {
				final Unit<?> unit = opSameUnit ? ((Amount) b).getUnit() : Unit.ONE;
				a = numToAmount((org.jscience.mathematics.number.Number) a, unit);
			} else if (b instanceof org.jscience.mathematics.number.Number) {
				final Unit<?> unit = opSameUnit ? ((Amount) a).getUnit() : Unit.ONE;
				b = numToAmount((org.jscience.mathematics.number.Number) b, unit);
			}
			result = calcAmount((Amount) a, op, (Amount) b);
		} else {
			// If either a or b is FloatingPoint, ensure both are
			if (a instanceof FloatingPoint || b instanceof FloatingPoint) {
				if (!(a instanceof FloatingPoint)) {
					a = FloatingPoint.valueOf(((org.jscience.mathematics.number.Number) a).doubleValue());
				}
				if (!(b instanceof FloatingPoint)) {
					b = FloatingPoint.valueOf(((org.jscience.mathematics.number.Number) b).doubleValue());
				}
			}
			// If either a or b is Rational, ensure both are
			if (a instanceof Rational || b instanceof Rational) {
				if (!(a instanceof Rational)) {
					a = Rational.valueOf(((org.jscience.mathematics.number.Number) a).longValue(), 1);
				}
				if (!(b instanceof Rational)) {
					b = Rational.valueOf(((org.jscience.mathematics.number.Number) b).longValue(), 1);
				}
			}

			result = calcNumber((org.jscience.mathematics.number.Number) a, op,
					(org.jscience.mathematics.number.Number) b);

			if (outputRadix != 10 && result instanceof LargeInteger) {
				result = new Radix(((LargeInteger) result).longValue(), outputRadix);
			}
		}
		if (result == null)
			return ParseResult.fail();
		else
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
	}

	@SuppressWarnings("rawtypes")
	private Amount numToAmount(final org.jscience.mathematics.number.Number num, final Unit<?> unit) {
		if (num.doubleValue() == num.longValue())
			return Amount.valueOf(num.longValue(), unit);
		else
			return Amount.valueOf(num.doubleValue(), unit);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object calcAmount(final Amount a, final String op, final Amount b) {
		if (op.equals("+"))
			return a.plus(b);
		else if (op.equals("-"))
			return a.minus(b);
		else if (op.equals("*"))
			return a.times(b);
		else if (op.equals("/") && b.getEstimatedValue() != 0){
			return a.divide(b);
		}
		else if (op.equals("^")) {
			if (b.getExactValue() != b.getEstimatedValue() || b.getExactValue() > Integer.MAX_VALUE)
				return null;
			else
				return a.pow((int) b.getExactValue());
		} else if (op.equals(">"))
			return a.isGreaterThan(b);
		else if (op.equals("<"))
			return a.isLessThan(b);
		else if (op.equals(">="))
			return a.approximates(b) || a.isGreaterThan(b);
		else if (op.equals("<="))
			return a.approximates(b) || a.isLessThan(b);
		else if (op.equals("="))
			return a.approximates(b);
		else if (op.equals("!="))
			return !a.approximates(b);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	private Object calcNumber(final org.jscience.mathematics.number.Number a, final String op,
			final org.jscience.mathematics.number.Number b) {
		if (op.equals("+"))
			return a.plus(b);
		else if (op.equals("-"))
			return a.minus(b);
		else if (op.equals("*")) {
			if (a instanceof LargeInteger && b instanceof LargeInteger)
				return a.times(b);
			else
				return FloatingPoint.valueOf(a.doubleValue()).times(FloatingPoint.valueOf(b.doubleValue()));
		}
		else if (op.equals("/")) {
			if (b.doubleValue() == 0.0)
				return null;
			if (a instanceof LargeInteger && b instanceof LargeInteger)
				return org.jscience.mathematics.number.Rational.valueOf((LargeInteger) a, (LargeInteger) b);
			else
				return FloatingPoint.valueOf(a.doubleValue() / b.doubleValue());
		} else if (op.equals("^")) {
			if (!(b instanceof LargeInteger) || ((LargeInteger) b).isNegative())
				return FloatingPoint.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
			else
				return a.pow(b.intValue());
		} else if (op.equals(">"))
			return a.isGreaterThan(b);
		else if (op.equals(">="))
			return a.equals(b) || a.isGreaterThan(b);
		else if (op.equals("<"))
			return a.isLessThan(b);
		else if (op.equals("<="))
			return a.equals(b) || a.isLessThan(b);
		else if (op.equals("mod")) {
			if (a instanceof LargeInteger && b instanceof LargeInteger) {
				final LargeInteger ali = (LargeInteger) a;
				final LargeInteger bli = (LargeInteger) b;
				return ali.mod(bli);
			} else
				return null;
		} else
			return null;
	}

	@Override
	public int hashCode() {
		return "MathBiOp".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof MathBiOp;
	}

}
