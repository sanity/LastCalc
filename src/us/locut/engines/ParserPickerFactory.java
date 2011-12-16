package us.locut.engines;

import java.io.Serializable;
import java.util.*;

import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

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

		public abstract ParseStep pickNext(ArrayList<Object> input, ParserContext context);

		protected ParseStep getNext(final ArrayList<Object> input, final ParserContext context,
				final Iterable<Parser> parsers) {
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
						if (parseResult.isSuccess() || parseResult.isError()) {
							prevAttemptPos.put(attempt, sPos);
							return new ParseStep(input, candidate, parseResult);
						}
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
		public ArrayList<Object> input;
		public Parser parser;

		public Attempt(final ArrayList<Object> input, final Parser parser) {
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
}
