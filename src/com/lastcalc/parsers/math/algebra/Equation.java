package com.lastcalc.parsers.math.algebra;

import com.lastcalc.parsers.math.RenderableAsMathML;

public class Equation implements RenderableAsMathML {
	public final Expression left, right;

	public final EqualityOp op;

	public Equation(final Expression left, final EqualityOp op, final Expression right) {
		this.left = left;
		this.right = right;
		this.op = op;
	}

	enum EqualityOp {
		EQUALS, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL, NOT_EQUAL
	}

	@Override
	public String toMathML() {
		// TODO Auto-generated method stub
		return null;
	}
}
