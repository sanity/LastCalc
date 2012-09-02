package com.lastcalc.parsers.math.algebra;


public class Variable extends Expression {

	public final String name;

	public Variable(final String name) {
		this.name = name;
	}

	@Override
	public String toMathML() {
		return "<mi>"+name+"</mi>";
	}

}
