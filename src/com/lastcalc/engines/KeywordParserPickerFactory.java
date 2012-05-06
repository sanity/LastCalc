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

import com.google.common.base.Function;
import com.google.common.collect.*;

import com.lastcalc.parsers.*;


public class KeywordParserPickerFactory extends ParserPickerFactory {
	private static final long serialVersionUID = -2346529366052255216L;
	private final Map<String, ParserSet> parsersMap;
	Set<Parser> noKeywords = new ParserSet();

	public KeywordParserPickerFactory(final Parser... parsers) {
		this(Lists.newArrayList(parsers));
	}

	public KeywordParserPickerFactory(final Iterable<Parser> parsers) {
		parsersMap = Maps.newHashMap();
		for (final Parser parser : parsers) {
			addParser(parser);
		}
	}

	@Override
	public void addParser(final Parser parser) {
		List<ParserSet> addTo = null;
		outer: for (final Object o : parser.getTemplate()) {
			if (o instanceof String) {
				ParserSet ll = parsersMap.get(o);
				if (ll == null) {
					ll = new ParserSet();
					parsersMap.put((String) o, ll);
				}
				if (addTo == null || addTo.size() > 1 || addTo.get(0).size() > ll.size()) {
					addTo = Collections.singletonList(ll);
				}
			} else if (o instanceof List) {
				final List<?> ol = (List<?>) o;
				final List<ParserSet> ma = Lists.newLinkedList();
				for (final Object oi : ol) {
					if (!(oi instanceof String)) {
						continue outer;
					}
					ParserSet ll = parsersMap.get(oi);
					if (ll == null) {
						ll = new ParserSet();
						parsersMap.put((String) oi, ll);
					}
					ma.add(ll);
				}
				if (addTo == null || ma.size() < addTo.size()) {
					addTo = ma;
				} else if (ma.size() == addTo.size()) {
					int maTtl = 0;
					for (final Set<Parser> ps : ma) {
						maTtl += ps.size();
					}
					int atTtl = 0;
					for (final Set<Parser> ps : addTo) {
						atTtl += ps.size();
					}
					if (maTtl < atTtl) {
						addTo = ma;
					}
				}

			}
		}
		if (addTo == null || addTo.isEmpty()) {
			noKeywords.add(parser);
		} else {
			for (final Set<Parser> sp : addTo) {
				sp.add(parser);
			}
		}
	}

	@Override
	public ParserPicker getPicker(final Map<Attempt, Integer> prevAttemptPos) {
		return new KeywordParserPicker(prevAttemptPos, parsersMap, noKeywords);
	}

	@Override
	public void teach(final Iterable<ParseStep> step) {
		// Noop
	}

	public static class KeywordParserPicker extends ParserPicker {

		private final Map<String, ParserSet> parsersMap;
		private final Set<Parser> noKeywords;

		public KeywordParserPicker(final Map<Attempt, Integer> prevAttemptPos,
				final Map<String, ParserSet> parsersMap, final Set<Parser> noKeywords) {
			super(prevAttemptPos);
			this.parsersMap = parsersMap;
			this.noKeywords = noKeywords;
		}

		@Override
		public ParseStep pickNext(final ParserContext context, final ParseStep previous) {
			Iterable<Parser> parsers = Iterables.concat(Iterables.transform(previous.result.output,
					new Function<Object, Iterable<Parser>>() {

				@Override
				public Iterable<Parser> apply(final Object token) {
					if ((!(token instanceof String)))
						return Collections.emptyList();
					final Set<Parser> parsers = parsersMap.get(token);
					if (parsers == null)
						return Collections.emptyList();
					else
						return parsers;
				}
			}));
			parsers = Iterables.concat(parsers, noKeywords);
			final ParseStep next = getNext(context, parsers, previous);
			return next;
		}

	}

	@Override
	public Collection<Parser> getParsers() {
		final Set<Parser> parsers = Sets.newHashSet();
		for (final Set<Parser> ps : parsersMap.values()) {
			parsers.addAll(ps);
		}
		return parsers;
	}

}
