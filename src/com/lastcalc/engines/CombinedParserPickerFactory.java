package com.lastcalc.engines;

import java.util.Map;

import com.google.common.collect.Lists;

import com.lastcalc.parsers.*;


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
		return new CombinedParserPicker(prevAttemptPos, factories);
	}

	@Override
	public void teach(final Iterable<ParseStep> step) {
		for (final ParserPickerFactory ppf : factories) {
			ppf.teach(step);
		}
	}

	public static class CombinedParserPicker extends ParserPicker {
		private final Iterable<ParserPickerFactory> ppfs;

		public CombinedParserPicker(final Map<Attempt, Integer> prevAttemptPos, final Iterable<ParserPickerFactory> ppfs) {
			super(prevAttemptPos);
			this.ppfs = ppfs;
		}

		@Override
		public ParseStep pickNext(final ParserContext context, final ParseStep previous) {
			for (final ParserPickerFactory ppf : ppfs) {
				final ParseStep ps = ppf.getPicker(prevAttemptPos).pickNext(context, previous);
				if (ps != null)
					return ps;
			}
			return null;
		}

	}

	@Override
	public void addParser(final Parser parser) {
		throw new UnsupportedOperationException();
	}

}
