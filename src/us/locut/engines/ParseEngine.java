package us.locut.engines;

import java.io.Serializable;
import java.util.*;

import us.locut.parsers.ParserContext;

public abstract class ParseEngine {

	public ArrayList<Object> parseAndGetLastStep(final ArrayList<Object> input,
			final ParserContext context) {
		return parse(input, context).getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final ArrayList<Object> input,
			ParserContext context);

}