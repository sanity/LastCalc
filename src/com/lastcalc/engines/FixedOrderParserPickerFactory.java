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

import com.google.common.collect.Lists;

import com.lastcalc.parsers.*;


public class FixedOrderParserPickerFactory extends ParserPickerFactory {
	private static final long serialVersionUID = -2346529366052255216L;
	private final LinkedList<Parser> parsers;

	public FixedOrderParserPickerFactory(final Parser... parsers) {
		this.parsers = Lists.newLinkedList(Lists.newArrayList(parsers));
	}

	public FixedOrderParserPickerFactory(final LinkedList<Parser> parsers) {
		this.parsers = parsers;
	}

	@Override
	public void addParser(final Parser parser) {
		parsers.add(parser);
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
		public ParseStep pickNext(final ParserContext context, final ParseStep previous) {
			final ParseStep next = getNext(context, parsers, previous);
			return next;
		}

	}

	public List<Parser> getParsers() {
		return parsers;
	}

}
