package us.locut.engines;

import java.util.*;

public abstract class ParseEngine {

	public ArrayList<Object> parseAndGetLastStep(final ArrayList<Object> input, final long maxTimeMillis) {
		return parse(input, maxTimeMillis).getLast().result.output;
	}

	public abstract LinkedList<ParseStep> parse(final ArrayList<Object> input, final long maxTimeMillis);

}