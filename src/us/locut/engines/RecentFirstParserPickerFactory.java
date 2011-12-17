package us.locut.engines;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.Iterables;

import us.locut.parsers.*;

public class RecentFirstParserPickerFactory extends ParserPickerFactory {

	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	private static final long serialVersionUID = -5521465155099741906L;

	LinkedList<Parser> parsers = new LinkedList<Parser>();

	private final int maxParsers;

	public RecentFirstParserPickerFactory(final Iterable<Parser> parsers) {
		this(parsers, Integer.MAX_VALUE);
	}

	public RecentFirstParserPickerFactory(final Iterable<Parser> parsers, final int maxParsers) {
		this.maxParsers = maxParsers;
		Iterables.addAll(this.parsers, parsers);
		while (this.parsers.size() > maxParsers) {
			this.parsers.removeLast();
		}
	}

	@Override
	public void teach(final Iterable<ParseStep> steps) {
		rwl.writeLock().lock();
		for (final ParseStep ps : steps) {
			// TODO: This is slow, probably need to use a better
			// datastructure for parsers, perhaps LinkedHashMap
			// or something
			parsers.remove(ps.parser);
			parsers.addFirst(ps.parser);
		}
		while (parsers.size() > maxParsers) {
			parsers.removeLast();
		}
		rwl.writeLock().unlock();
	}


	@Override
	public ParserPicker getPicker(final Map<Attempt, Integer> prevAttemptPos) {
		return new ParserPicker(prevAttemptPos) {

			@Override
			public ParseStep pickNext(final ParserContext context, final ParseStep previous,
					final int createOrder) {
				// matchTemplate
				rwl.readLock().lock();
				final ParseStep nextParseStep = getNext(context, parsers, previous, createOrder);
				rwl.readLock().unlock();
				return nextParseStep;
			}
		};
	}

}
