package us.locut.engines;

import java.util.*;
import java.util.Map.Entry;

import us.locut.TokenList;
import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;
import us.locut.parsers.PreParser.ListWithTail;
import us.locut.parsers.PreParser.MapWithTail;

public class ParseStep implements Comparable<ParseStep> {
	public final TokenList input;

	public final ParseResult result;

	public final Parser parser;

	public final ParseStep previous;

	public final int depth;

	public final int createOrder;

	public final double scoreBias;

	public ParseStep(final TokenList input, final Parser parser, final ParseResult result, final ParseStep previous,
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
		return input + " -> " + parser.getClass().getSimpleName() + " : " + parser + " -> " + result.output;
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
			cachedScore = getScore(result.output) + scoreBias;
		}
		return cachedScore;
	}

	public static double getScore(final Object output) {
		if (output instanceof Iterable) {
			double score = 0;
			for (final Object o : ((Iterable<Object>) output)) {
				score += getScore(o);
			}
			return score;
		} else if (output instanceof String)
			return 1;
		else if (output instanceof Map) {
			final Map<Object, Object> map = (Map<Object, Object>) output;
			double score = 0;
			for (final Map.Entry<Object, Object> e : map.entrySet()) {
				score += getScore(e.getKey()) + getScore(e.getValue());
			}
			return score;
		} else if (output instanceof MapWithTail) {
			final MapWithTail mwt = (MapWithTail) output;
			return 1 + getScore(mwt.map) + getScore(mwt.tail);
		} else if (output instanceof ListWithTail) {
			final ListWithTail lwt = (ListWithTail) output;
			return 1 + getScore(lwt.list) + getScore(lwt.tail);
		} else
			return 0;
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
		} else if (o instanceof ListWithTail)
			return false;
		return true;
	}
}

