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

import com.google.common.collect.*;

import com.lastcalc.TokenList;
import com.lastcalc.engines.ParserPickerFactory.ParserPicker;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.Parser.ParseResult;


public class BacktrackingParseEngine extends ParseEngine {

	private final ParserPickerFactory ppf;

	public BacktrackingParseEngine(final ParserPickerFactory ppf) {
		this.ppf = ppf;

	}

	private int lastParseStepCount = 0;

	private boolean dumpSteps = false;

	public void setDumpSteps(final boolean dumpSteps) {
		this.dumpSteps = dumpSteps;
	}

	@Override
	public LinkedList<ParseStep> parse(final TokenList input,
			final ParserContext context,
			final TokenList... alternateInputs) {
		lastParseStepCount = 0;
		final TreeSet<ParseStep> candidates = Sets.<ParseStep> newTreeSet();
		final ParserPicker picker = ppf.getPicker();
		candidates.add(new ParseStep(input, NoopParser.singleton, ParseResult.success(input), null, 0));
		for (final TokenList alternateTL : alternateInputs) {
			candidates.add(new ParseStep(alternateTL, NoopParser.singleton, ParseResult.success(alternateTL), null, 0));
		}
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
				final ParseStep nextStep = picker.pickNext(subContext, candidateStep);
				if (nextStep != null && nextStep.result.isSuccess()) {
					lastParseStepCount++;
					if (dumpSteps) {
						System.out.println(lastParseStepCount + "\t" + nextStep);
					}
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

		bestStep.isMinimal();

		while (bestStep != null) {
			steps.addFirst(bestStep);
			bestStep = bestStep.previous;
		}

		return steps;
	}

	public int getLastParseStepCount() {
		return lastParseStepCount;
	}
}

