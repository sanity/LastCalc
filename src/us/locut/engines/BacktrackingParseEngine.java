package us.locut.engines;

import java.util.*;

import com.google.common.collect.*;

import us.locut.TokenList;
import us.locut.engines.ParserPickerFactory.ParserPicker;
import us.locut.parsers.*;
import us.locut.parsers.Parser.ParseResult;

public class BacktrackingParseEngine extends ParseEngine {

	private final ParserPickerFactory ppf;

	public BacktrackingParseEngine(final ParserPickerFactory ppf) {
		this.ppf = ppf;

	}

	@Override
	public LinkedList<ParseStep> parse(final TokenList input,
			final ParserContext context) {
		final TreeSet<ParseStep> candidates = Sets.<ParseStep> newTreeSet();
		final ParserPicker picker = ppf.getPicker();
		final int createOrder = 0;
		candidates.add(new ParseStep(input, NoopParser.singleton, ParseResult.success(input), null, createOrder, 0));
		final long startTime = System.currentTimeMillis();
		ParserContext subContext;
		try {
			subContext = (ParserContext) context.clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		subContext.timeout = context.timeout / 5;
		final Set<ParseStep> exhausted = Sets.newHashSet();
		outer: while (System.currentTimeMillis() - startTime < context.timeout && !candidates.first().isMinimal()) {
			for (final ParseStep candidateStep : candidates) {
				if (exhausted.contains(candidateStep)) {
					continue;
				}
				final ParseStep nextStep = picker.pickNext(subContext, candidateStep,
						createOrder);
				if (nextStep != null && nextStep.result.isSuccess()) {
					candidates.add(nextStep);
					continue outer;
				} else {
					exhausted.add(candidateStep);
				}
			}
			break outer;
		}
		final LinkedList<ParseStep> steps = Lists.newLinkedList();
		ParseStep bestStep = candidates.first();
		while (bestStep != null) {
			steps.addFirst(bestStep);
			bestStep = bestStep.previous;
		}
		return steps;
	}
}

