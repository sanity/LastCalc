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

import com.google.common.collect.Lists;

import org.junit.*;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;


public class CombinedParserPickerFactoryTest {

	@Test
	public void test() {
		final Parser p1 = new RewriteParser(Lists.newArrayList("1", "1"), "one");
		final Parser p2 = new RewriteParser(Lists.newArrayList("2", "2"), "two");
		final Parser p3 = new RewriteParser(Lists.newArrayList("3", "3"), "three");
		final Parser p4 = new RewriteParser(Lists.newArrayList("4", "4"), "four");

		final FixedOrderParserPickerFactory f1 = new FixedOrderParserPickerFactory(p1, p2);
		final FixedOrderParserPickerFactory f2 = new FixedOrderParserPickerFactory(p3, p4);
		final CombinedParserPickerFactory f = new CombinedParserPickerFactory(f1, f2);

		final BacktrackingParseEngine pe = new BacktrackingParseEngine(f);

		final ParserContext context = new ParserContext(pe, Long.MAX_VALUE);
		final TokenList result = pe.parseAndGetLastStep(new TokenList.SimpleTokenList("1", "1", "2", "2", "3", "3",
				"4", "4"),
				context);

		Assert.assertEquals(TokenList.createD("one", "two", "three", "four"), result);
	}

}
