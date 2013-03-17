package com.lastcalc.parsers.math;

import com.google.common.collect.Lists;

import org.jscience.mathematics.number.LargeInteger;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

public class BitwiseOp extends Parser {
	private static final long serialVersionUID = -694604478564851560L;
	private final TokenList template = TokenList.createD(Lists.newArrayList(LargeInteger.class, Radix.class),
			Lists.newArrayList("xor", "and", "or"), Lists.newArrayList(LargeInteger.class, Radix.class));

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Object ar = tokens.get(templatePos);
		final long a = ar instanceof LargeInteger ? ((LargeInteger) ar).longValue() : ((Radix) ar).integer;
		final String op = (String) tokens.get(templatePos + 1);
		final Object br = tokens.get(templatePos + 2);
		final long b = br instanceof LargeInteger ? ((LargeInteger) br).longValue() : ((Radix) br).integer;
		int radix = 10;
		if (ar instanceof Radix && br instanceof Radix && ((Radix) ar).radix == ((Radix) br).radix) {
			radix = ((Radix) ar).radix;
		}
		long result;
		if (op.equals("xor")) {
			result = a ^ b;
		} else if (op.equals("and")) {
			result = a & b;
		} else if (op.equals("or")) {
			result = a | b;
		} else
			return ParseResult.fail();
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
				radix == 10 ? LargeInteger.valueOf(result) : new Radix(result, radix)));
	}

	@Override
	public int hashCode() {
		return "BitwiseOp".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BitwiseOp;
	}

}
