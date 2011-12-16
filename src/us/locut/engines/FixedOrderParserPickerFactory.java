package us.locut.engines;

import java.util.*;

import us.locut.parsers.*;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class FixedOrderParserPickerFactory extends ParserPickerFactory {
	private static final long serialVersionUID = -2346529366052255216L;
	private final Iterable<Parser> parsers;

	public FixedOrderParserPickerFactory(final Parser... parsers) {
		this.parsers = Lists.newArrayList(parsers);
	}

	public FixedOrderParserPickerFactory(final Iterable<Parser> parsers) {
		this.parsers = parsers;
	}

	@Override
	public ParserPicker getPicker(final Map<Attempt, Integer> prevAttemptPos) {
		return new FixedOrderParserPicker(prevAttemptPos, parsers);
	}

	@Override
	public void teach(final Iterable<ParseStep> step) {
		// Noop
	}

	public static class FixedOrderParserPicker extends ParserPicker {

		private final Iterable<Parser> parsers;

		public FixedOrderParserPicker(final Map<Attempt, Integer> prevAttemptPos, final Iterable<Parser> parsers) {
			super(prevAttemptPos);
			this.parsers = parsers;
		}

		@Override
		public ParseStep pickNext(final List<Object> input, final ParserContext context) {
			final ParseStep next = getNext(input, context, parsers);
			return next;
		}

	}

}
