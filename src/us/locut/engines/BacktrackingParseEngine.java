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
		System.out.println("Parsing: " + input);
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
		outer: while (System.currentTimeMillis() - startTime < context.timeout
				&& candidates.first().result.output.size() > 1) {
			for (final ParseStep candidateStep : candidates) {
				final ParseStep nextStep = picker.pickNext(subContext, candidateStep,
						createOrder);
				if (nextStep != null && nextStep.result.isSuccess()) {
					System.out.println("   " + nextStep.result.output);
					candidates.add(nextStep);
					continue outer;
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

