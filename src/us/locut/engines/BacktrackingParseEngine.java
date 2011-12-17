package us.locut.engines;

import java.util.*;

import com.google.common.collect.*;

import us.locut.engines.ParserPickerFactory.ParserPicker;
import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

public class BacktrackingParseEngine extends ParseEngine {

	private final ParserPickerFactory ppf;

	public BacktrackingParseEngine(final ParserPickerFactory ppf) {
		this.ppf = ppf;

	}

	@Override
	public LinkedList<ParseStep> parse(final List<Object> input,
			final ParserContext context) {
		final TreeSet<ParseStep> candidates = Sets.<ParseStep> newTreeSet();
		final ParserPicker picker = ppf.getPicker();
		final int createOrder = 0;
		candidates.add(new ParseStep(input, NoopParser.singleton, ParseResult.success(input), null, createOrder));
		outer: while (System.currentTimeMillis() < context.terminateTime) {
			for (final ParseStep candidateStep : candidates) {
				final ParseStep nextStep = picker.pickNext(context, candidateStep,
						createOrder);
				if (nextStep != null && nextStep.result.isSuccess()) {
					candidates.add(nextStep);
					continue outer;
				}
			}
			break outer;
		}
		final LinkedList<ParseStep> steps = Lists.newLinkedList();
		ParseStep bestStep = candidates.first();
		while (!(bestStep.parser instanceof NoopParser)) {
			steps.addFirst(bestStep);
			bestStep = bestStep.previous;
		}
		return steps;
	}
}

