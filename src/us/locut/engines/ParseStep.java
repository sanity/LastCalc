package us.locut.engines;

import java.util.ArrayList;

import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

public class ParseStep {
	public final ArrayList<Object> input;

	public final ParseResult result;

	public final Parser parser;

	public ParseStep(final ArrayList<Object> input, final Parser parser, final ParseResult result) {
		this.input = input;
		this.parser = parser;
		this.result = result;
	}

	@Override
	public String toString() {
		return input + " -> " + parser.getClass().getSimpleName() + " -> " + result.output;
	}
}
