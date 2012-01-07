package us.locut.engines;

import java.util.LinkedList;

import us.locut.TokenList;
import us.locut.parsers.ParserContext;

public abstract class ParseEngine {

	public TokenList parseAndGetLastStep(final TokenList input,
			final ParserContext context) {
		final LinkedList<ParseStep> parse = parse(input, context);
		if (parse.isEmpty())
			throw new RuntimeException("Parse resulted in no ParseSteps");
		return parse.getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final TokenList input,
			ParserContext context);

}