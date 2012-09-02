package com.lastcalc.parsers.math.algebra;

import com.lastcalc.parsers.math.RenderableAsMathML;

public abstract class Expression implements RenderableAsMathML {

	/**
	 * Returns an evaluated version of this expression, if possible.
	 * ie. Evaluating "3+5*2" will return 13.
	 * @return
	 */
	public abstract Expression evaluate();
}
