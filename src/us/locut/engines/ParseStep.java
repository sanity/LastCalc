package us.locut.engines;

import java.util.ArrayList;

import us.locut.parsers.Parser.ParseResult;

public class ParseStep {
	public final ArrayList<Object> input, output;

	public final ParseResult result;

	public ParseStep(final ArrayList<Object> input, final ParseResult result) {
		this.input = input;
		output = result.output;
		this.result = result;
	}
}
