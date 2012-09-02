package com.lastcalc.parsers.math.algebra;

public class Number extends Expression {

	public final java.lang.Number number;

	public Number(final java.lang.Number number) {
		this.number = number;
	}

	@Override
	public String toMathML() {
		return "<mi>"+number+"</mi>";
	}

	@Override
	public Expression evaluate() {
		return this;
	}

}
