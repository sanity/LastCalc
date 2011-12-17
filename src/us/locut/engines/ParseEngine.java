package us.locut.engines;

import java.util.*;

import us.locut.parsers.ParserContext;

import com.google.common.collect.Lists;

public abstract class ParseEngine {

	public List<Object> parseAndGetLastStep(final List<Object> input,
			final ParserContext context) {
		final LinkedList<ParseStep> parse = parse(input, context);
		if (parse.isEmpty())
			return Lists.newArrayList();
		return parse.getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final List<Object> input,
			ParserContext context);

}