package us.locut.engines;

import java.io.Serializable;
import java.util.*;

import us.locut.parsers.*;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

public abstract class ParserPickerFactory implements Serializable {

	private static final long serialVersionUID = -1641261497813404734L;

	public ParserPicker getPicker() {
		return getPicker(Maps.<Attempt, Integer> newHashMap());
	}

	public abstract ParserPicker getPicker(Map<Attempt, Integer> prevAttemptPos);

	public abstract void teach(Iterable<ParseStep> step);

	public static abstract class ParserPicker {

		public abstract ParseStep pickNext(ArrayList<Object> input, ParserContext context);
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
