package us.locut.engines;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

import com.google.appengine.repackaged.com.google.common.collect.*;

public class RecentFirstParserPickerFactory implements ParserPickerFactory {

	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	private static final long serialVersionUID = -5521465155099741906L;

	LinkedHashSet<Parser> parsers = Sets.newLinkedHashSet();

	public RecentFirstParserPickerFactory(final Iterable<Parser> parsers) {
		Iterables.addAll(this.parsers, parsers);
	}

	public void teach(final List<ParseStep> step) {
		rwl.writeLock().lock();

		rwl.writeLock().unlock();
	}


	@Override
	public ParserPicker getPicker() {
		return new ParserPicker() {

			@Override
			public ParseStep pickNext(final ArrayList<Object> input) {
				rwl.readLock().lock();
				for (final Parser candidate : parsers) {
					int sPos = -1;
					templateScan : while (true) {
						sPos = candidate.matchTemplate(input, sPos+1);
						if (sPos != -1) {
							final ParseResult parseResult = candidate.parse(input, sPos);
							if (parseResult.isSuccess() || parseResult.isError()) {
								rwl.readLock().unlock();
								return new ParseStep(input, parseResult);
							}
						} else {
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
