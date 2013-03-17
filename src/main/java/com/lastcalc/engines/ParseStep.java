/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
package com.lastcalc.engines;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.Parser.ParseResult;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

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

	private double cachedScore = Double.MAX_VALUE;

	private double getScore() {
		if (cachedScore == Double.MAX_VALUE) {
			cachedScore = scoreBias + getScore(result.output) - (0.0001 * depth);
		}
		return cachedScore;
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
		if (result.output.size() == 1 && !(result.output.get(0) instanceof String)) {
			if (result.output.get(0) instanceof UserDefinedParser) {
				final UserDefinedParser udp = (UserDefinedParser) result.output.get(0);
				// Handle a common case
				if (udp.getTemplate().indexOf("if") != -1)
					return false;
				else
					return true;
			} else
				return true;
		} else
			return false;
	}
}
