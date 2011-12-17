package us.locut.engines;

import java.util.List;

import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;
import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;

public class ParseStep implements Comparable<ParseStep> {
	public final List<Object> input;

	public final ParseResult result;

	public final Parser parser;

	public final ParseStep previous;

	public final int depth;

	public final int createOrder;

	public final double scoreBias;

	public ParseStep(final List<Object> input, final Parser parser, final ParseResult result, final ParseStep previous,
			final int createOrder) {
		this.input = input;
		this.parser = parser;
		scoreBias = (previous == null ? 0 : previous.scoreBias) + parser.getScoreBias();
		this.result = result;
		this.previous = previous;
		this.createOrder = createOrder;
		if (previous != null) {
			depth = previous.depth + 1;
		} else {
			depth = 0;
		}
	}

	@Override
	public String toString() {
		return alParse(input) + " -> " + parser.getClass().getSimpleName() + " : " + parser + " -> "
				+ alParse(result.output);
	}

	private static String alParse(final List<Object> input) {
		final StringBuilder sb = new StringBuilder();
		for (final Object o : input) {
			sb.append(o.getClass().getSimpleName() + "[" + o.toString() + "] ");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(final ParseStep other) {
		if (getScore() < other.getScore())
			return -1;
		else if (getScore() > other.getScore())
			return 1;
		else if (hashCode() < other.hashCode())
			return -1;
		else if (hashCode() > other.hashCode())
			return 1;
		else
			return 0;
	}

	private double getScore() {
		if (parser instanceof UserDefinedParser) {
			final UserDefinedParser udp = (UserDefinedParser) parser;
			return scoreBias + udp.after.size();
		} else
			return scoreBias + result.output.size();
	}
}
