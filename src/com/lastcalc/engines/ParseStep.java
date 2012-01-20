package com.lastcalc.engines;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.Parser.ParseResult;

public class ParseStep implements Comparable<ParseStep> {
	public final TokenList input;

	public final ParseResult result;

	public final Parser parser;

	public final ParseStep previous;

	public final int depth;

	public final double scoreBias;

	public ParseStep(final TokenList input, final Parser parser, final ParseResult result, final ParseStep previous,
			final double score) {
		this.input = input;
		this.parser = parser;
		scoreBias = (previous == null ? 0 : previous.scoreBias) + score;
		this.result = result;
		this.previous = previous;
		if (previous != null) {
			depth = previous.depth + 1;
		} else {
			depth = 0;
		}
	}

	@Override
	public String toString() {
		return getScore() + "\t" + parser.getClass().getSimpleName() + "\t" + input + "\t" + result.output;
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
		return scoreBias + getScore(result.output) - (0.0001 * depth);
	}

	public static double getScore(final Object token) {
		double score = 0;
		if (token instanceof TokenList) {
			for (final Object t : ((TokenList) token)) {
				if (t instanceof String) {
					score++;
				} else if (t instanceof Number) {
					score += 0.8;

				} else {
					score += 0.5;
				}
			}
		}
		return score;
	}

	public boolean isMinimal() {
		return result.output.size() == 1;
	}
}
