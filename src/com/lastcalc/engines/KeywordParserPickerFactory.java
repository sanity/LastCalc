package com.lastcalc.engines;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.collect.*;

import com.lastcalc.parsers.*;


public class KeywordParserPickerFactory extends ParserPickerFactory {
	private static final long serialVersionUID = -2346529366052255216L;
	private final Map<String, Set<Parser>> parsersMap;
	Set<Parser> noKeywords = Sets.newHashSet();

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
		boolean foundKeyword = false;
		for (final Object o : parser.getTemplate()) {
			if (o instanceof String) {
				foundKeyword = true;
				Set<Parser> ll = parsersMap.get(o);
				if (ll == null) {
					ll = Sets.newHashSet();
					parsersMap.put((String) o, ll);
				}
				ll.add(parser);
			}
		}
		if (!foundKeyword) {
			noKeywords.add(parser);
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

		private final Map<String, Set<Parser>> parsersMap;
		private final Set<Parser> noKeywords;

		public KeywordParserPicker(final Map<Attempt, Integer> prevAttemptPos,
				final Map<String, Set<Parser>> parsersMap, final Set<Parser> noKeywords) {
			super(prevAttemptPos);
			this.parsersMap = parsersMap;
			this.noKeywords = noKeywords;
		}

		@Override
		public ParseStep pickNext(final ParserContext context, final ParseStep previous) {
			final Iterable<Parser> parsers = Iterables.concat(Iterables.transform(previous.result.output,
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
			final ParseStep next = getNext(context, Iterables.concat(parsers, noKeywords), previous);
			return next;
		}

	}

}
