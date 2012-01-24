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
			"=", "!=");

	private static String[] precidence = new String[] { "^", "*", "/", "%", "+", "-", "<", ">", "<=", ">=", "=", "!=" };

	private static Map<String, Integer> precidenceMap = Maps.newHashMap();
	static {
		for (int x = 0; x < precidence.length; x++) {
			precidenceMap.put(precidence[x], x);
		}
	}

	private static TokenList template = TokenList.createD(
			Object.class,
			Lists.<Object>newArrayList(precidence),
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
		if ((!(a instanceof org.jscience.mathematics.number.Number) && !(a instanceof Amount))
				|| (!(b instanceof org.jscience.mathematics.number.Number) && !(b instanceof Amount)))
			return ParseResult.fail();
		final String op = (String) tokens.get(templatePos + 1);

		final Integer opPrecidence = precidenceMap.get(op);
		for (final Object token : PreParser.enclosedByStructure(tokens, templatePos)) {
			final Integer tPrecidence = precidenceMap.get(token);
			if (tPrecidence != null && tPrecidence < opPrecidence)
				return Parser.ParseResult.fail();
		}

		final Object result;
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
		}
		if (result == null)
			return ParseResult.fail();
		else
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
	}

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
		else if (op.equals("/"))
			return a.divide(b);
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
			if (a instanceof LargeInteger && b instanceof LargeInteger)
				return org.jscience.mathematics.number.Rational.valueOf((LargeInteger) a, (LargeInteger) b);
			else
				return FloatingPoint.valueOf(a.doubleValue() / b.doubleValue());
		} else if (op.equals("^")) {
			if (b instanceof FloatingPoint)
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
		else if (op.equals("="))
			return a.equals(b);
		else if (op.equals("!="))
			return !a.equals(b);
		else
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
