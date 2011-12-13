package us.locut.engines;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

import com.google.appengine.repackaged.com.google.common.collect.Iterables;

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
		return new ParserPicker() {

			@Override
			public ParseStep pickNext(final ArrayList<Object> input) {
				int sPos = -1; // Decrement because we add 1 before each call to
				// matchTemplate
				rwl.readLock().lock();
				for (final Parser candidate : parsers) {
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
							// complete
							// our scan of the template, start again where we
							// left off
							sPos = ssPos;
						}
					}
					templateScan : while (true) {
						sPos = candidate.matchTemplate(input, sPos+1);
						if (sPos != -1) {
							final ParseResult parseResult = candidate.parse(input, sPos);
							if (parseResult.isSuccess() || parseResult.isError()) {
								rwl.readLock().unlock();
								prevAttemptPos.put(attempt, sPos);
								return new ParseStep(input, candidate, parseResult);
							}
						} else {
							prevAttemptPos.put(attempt, -2);
							break templateScan;
						}
					}
				}
				rwl.readLock().unlock();
				return null;
			}
		};
	}

}
