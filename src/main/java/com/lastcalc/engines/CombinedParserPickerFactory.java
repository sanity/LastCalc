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

import java.util.*;

import com.google.common.collect.*;

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
	public Collection<Parser> getParsers() {
		final Set<Parser> parsers = Sets.newHashSet();
		for (final ParserPickerFactory ppf : factories) {
			parsers.addAll(ppf.getParsers());
		}
		return parsers;
	}

	@Override
	public void addParser(final Parser parser) {
		throw new UnsupportedOperationException();
	}

}
