package us.locut.engines;

import java.util.*;

import us.locut.parsers.ParserContext;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public abstract class ParseEngine {

	public ArrayList<Object> parseAndGetLastStep(final ArrayList<Object> input,
			final ParserContext context) {
		final LinkedList<ParseStep> parse = parse(input, context);
		if (parse.isEmpty())
			return Lists.newArrayList();
		return parse.getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final ArrayList<Object> input,
			ParserContext context);

}