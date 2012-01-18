package com.lastcalc.engines;

import java.util.LinkedList;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.ParserContext;


public abstract class ParseEngine {

	public TokenList parseAndGetLastStep(final TokenList input,
 final ParserContext context,
			final TokenList... alternateInputs) {
		final LinkedList<ParseStep> parse = parse(input, context, alternateInputs);
		if (parse.isEmpty())
			throw new RuntimeException("Parse resulted in no ParseSteps");
		return parse.getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final TokenList input,
			ParserContext context,
			TokenList... alternateInputs);

}