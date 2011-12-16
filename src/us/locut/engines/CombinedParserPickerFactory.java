package us.locut.engines;

import java.util.*;

import us.locut.parsers.ParserContext;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class CombinedParserPickerFactory extends ParserPickerFactory {
	private static final long serialVersionUID = -9047872184877535738L;
	private final Iterable<ParserPickerFactory> factories;

	public CombinedParserPickerFactory(final ParserPickerFactory... factories) {
		this(Lists.newArrayList(factories));
	}

	public CombinedParserPickerFactory(final Iterable<ParserPickerFactory> factories) {
		this.factories = factories;
	}

	@Override
	public ParserPicker getPicker(final Map<Attempt, Integer> prevAttemptPos) {
		return new CombinedParserPicker(prevAttemptPos, factories.iterator());
	}

	@Override
	public void teach(final Iterable<ParseStep> step) {
		for (final ParserPickerFactory ppf : factories) {
			ppf.teach(step);
		}
	}

	public static class CombinedParserPicker extends ParserPicker {
		private final Iterator<ParserPickerFactory> ppfs;
		private ParserPicker current;

		public CombinedParserPicker(final Map<Attempt, Integer> prevAttemptPos, final Iterator<ParserPickerFactory> ppfs) {
			super(prevAttemptPos);
			this.ppfs = ppfs;
			if (ppfs.hasNext()) {
				current = ppfs.next().getPicker(prevAttemptPos);
			} else {
				current = null;
			}
		}

		@Override
		public ParseStep pickNext(final List<Object> input, final ParserContext context) {
			if (current == null)
				return null;
			while (true) {
				final ParseStep next = current.pickNext(input, context);
				if (next != null)
					return next;
				if (!ppfs.hasNext())
					return null;
				current = ppfs.next().getPicker(prevAttemptPos);
			}
		}

	}

}
