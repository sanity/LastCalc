package us.locut.engines;

import java.util.*;
import java.util.Map.Entry;

import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;
import us.locut.parsers.PreParser.SubTokenSequence;

public class ParseStep implements Comparable<ParseStep> {
	public final List<Object> input;

	public final ParseResult result;

	public final Parser parser;

	public final ParseStep previous;

	public final int depth;

	public final int createOrder;

	public final double scoreBias;

	public ParseStep(final List<Object> input, final Parser parser, final ParseResult result, final ParseStep previous,
			final int createOrder, final double score) {
		this.input = input;
		this.parser = parser;
		scoreBias = (previous == null ? 0 : previous.scoreBias) + score;
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

	double cachedScore = Double.MIN_VALUE;

	private double getScore() {
		if (cachedScore == Double.MIN_VALUE) {
			cachedScore = scoreBias + PreParser.flatten(result.output).size() + result.output.size();
		}
		return cachedScore;
	}

	public boolean isMinimal() {
		return isMinimal(result.output);
	}

	private boolean isMinimal(final Object o) {
		if (o instanceof Iterable) {
			for (final Object r : ((Iterable<Object>) o)) {
				if (!isMinimal(r))
					return false;
			}
			return true;
		} else if (o instanceof String)
			return false;
		else if (o instanceof Map) {
			for (final Entry<Object, Object> e : ((Map<Object, Object>) o).entrySet()) {
				if (!isMinimal(e.getKey()) || !isMinimal(e.getValue()))
					return false;
			}
		} else if (o instanceof SubTokenSequence)
			return isMinimal(((SubTokenSequence) o).tokens);
		return true;
	}
}

