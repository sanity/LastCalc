package com.lastcalc.parsers.math.algebra;


public class BiOp extends Expression {

	public final Expression left, right;

	Op op;

	public BiOp(final Expression left, final Op op, final Expression right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	enum Op {
		PLUS, MINUS, MULTIPLY_BY, DIVIDE_BY, TO_POWER_OF
	}

	@Override
	public String toMathML() {
		return null;
	}

}
