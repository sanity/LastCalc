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
package com.lastcalc.parsers.collections;

import java.util.LinkedList;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import com.lastcalc.*;
import com.lastcalc.engines.*;
import com.lastcalc.parsers.*;


public class CollectionsTest {

	// @Test
	public void getFromMapTest() {
		final PreParser bp = new PreParser();
		final TokenList origTokens = Tokenizer.tokenize("get b from {a:A, b:B}");
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		com.lastcalc.parsers.Parser.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new PreParser());
		priorityParsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final ParseEngine st = new BacktrackingParseEngine(new CombinedParserPickerFactory(priorityPPF, catchAllPPF));
		final ParserContext context = new ParserContext(st, Long.MAX_VALUE);
		final TokenList result = st.parseAndGetLastStep(origTokens, context);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("B", result.get(0));
	}
}
