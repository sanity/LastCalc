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

import java.io.Serializable;
import java.util.*;

import com.google.common.collect.Maps;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.Parser.ParseResult;


public abstract class ParserPickerFactory implements Serializable {

	private static final long serialVersionUID = -1641261497813404734L;

	public ParserPicker getPicker() {
		return getPicker(Maps.<Attempt, Integer> newHashMap());
	}

	public abstract ParserPicker getPicker(Map<Attempt, Integer> prevAttemptPos);

	public abstract void teach(Iterable<ParseStep> step);

	public static abstract class ParserPicker {

		protected final Map<Attempt, Integer> prevAttemptPos;

		public ParserPicker(final Map<Attempt, Integer> prevAttemptPos) {
			this.prevAttemptPos = prevAttemptPos;

		}

		public abstract ParseStep pickNext(ParserContext context, ParseStep previous);

		protected ParseStep getNext(final ParserContext context,
				final Iterable<Parser> parsers,
				final ParseStep previous) {
			final TokenList input = previous.result.output;
			for (final Parser candidate : parsers) {
				int sPos = -1;
				final Attempt attempt = new Attempt(input, candidate);
				// Check to see if we've tried applying this parser to these
				// input tokens before
				final Integer ssPos = prevAttemptPos.get(attempt);
				if (ssPos != null) {
					if (ssPos == -2) {
						// Yes, we've tried this before, move on to the next
						// candidate
						continue;
					} else {
						// We've tried this parser on this input but didn't
						// complete our scan of the template, start again
						// where we left off
						sPos = ssPos;
					}
				}
				templateScan: while (true) {
					sPos = candidate.matchTemplate(input, sPos + 1);
					if (sPos != -1) {
						final ParseResult parseResult = candidate.parse(input, sPos, context);
						prevAttemptPos.put(attempt, sPos);
						if (parseResult.isSuccess())
							return new ParseStep(input, candidate, parseResult, previous,
									parseResult.scoreBias);
					} else {
						prevAttemptPos.put(attempt, -2);
						break templateScan;
					}
				}
			}
			return null;
		}

	}


	public static class Attempt {
		public TokenList input;
		public Parser parser;

		public Attempt(final TokenList input, final Parser parser) {
			this.input = input;
			this.parser = parser;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + input.hashCode();
			result = prime * result + ((parser == null) ? 0 : parser.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Attempt))
				return false;
			final Attempt other = (Attempt) obj;
			if (!input.equals(other.input))
				return false;
			if (parser == null) {
				if (other.parser != null)
					return false;
			} else if (!parser.equals(other.parser))
				return false;
			return true;
		}

	}

	public abstract void addParser(Parser parser);

	public abstract Collection<Parser> getParsers();
}
