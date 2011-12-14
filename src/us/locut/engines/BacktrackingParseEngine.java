package us.locut.engines;

import java.io.Serializable;
import java.util.*;

import us.locut.engines.ParserPickerFactory.ParserPicker;
import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

import com.google.appengine.repackaged.com.google.common.collect.*;

public class BacktrackingParseEngine extends ParseEngine {

	private final ParserPickerFactory ppf;

	public BacktrackingParseEngine(final ParserPickerFactory ppf) {
		this.ppf = ppf;

	}

	@Override
	public LinkedList<ParseStep> parse(final ArrayList<Object> input,
			final ParserContext context) {
		final TreeSet<BTParseStep> candidates = Sets.newTreeSet();
		final long startTime = System.currentTimeMillis();
		final ParserPicker picker = ppf.getPicker();
		outer: while (System.currentTimeMillis() < context.terminateTime) {
			if (candidates.isEmpty()) {
				final ParseStep nextStep = picker.pickNext(input, context);
				if (nextStep == null)
					return null;
				else {
					candidates.add(new BTParseStep(nextStep, null));
				}
			}
			if (candidates.first().result.output.size() == 1) {
				// Can't do better than this
				break outer;
			}
			for (final BTParseStep candidateStep : candidates) {
				final ParseStep nextStep = picker.pickNext(candidateStep.result.output, context);
				if (nextStep != null && nextStep.result.isSuccess()) {
					candidates.add(new BTParseStep(nextStep, candidateStep));
					continue outer;
				}
			}
			break outer;
		}
		BTParseStep bestStep = candidates.first();
		final LinkedList<ParseStep> steps = Lists.newLinkedList();
		while (bestStep != null) {
			steps.addFirst(bestStep);
			bestStep = bestStep.previous;
		}
		return steps;
	}

	public static class BTParseStep extends ParseStep implements Comparable<BTParseStep> {

		public final BTParseStep previous;

		public BTParseStep(final ParseStep parseStep, final BTParseStep previous) {
			super(parseStep.input, parseStep.parser, parseStep.result);
			this.previous = previous;
		}

		public BTParseStep(final ArrayList<Object> input, final Parser parser, final ParseResult result,
				final BTParseStep previous) {
			super(input, parser, result);
			this.previous = previous;
		}

		@Override
		public int compareTo(final BTParseStep o) {
			if (result.output.size() < o.result.output.size())
				return -1;
			else if (result.output.size() > o.result.output.size())
				return 1;
			else
				return Double.compare(result.output.hashCode(), o.result.output.hashCode());
		}

	}
}
