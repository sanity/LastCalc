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
package com.lastcalc;

import java.util.LinkedList;

import javax.measure.unit.Unit;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import org.jscience.physics.amount.Amount;

import com.lastcalc.engines.*;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;


public class ParseEngineTest {

	// @Test
	public void operatorPrecidenceTest() {
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		com.lastcalc.parsers.Parser.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new PreParser());
		// priorityParsers.addAll(AmountMathOp.getOps());
		parsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final CombinedParserPickerFactory globalParserPickerFactory = new CombinedParserPickerFactory(priorityPPF,
				catchAllPPF);
		final ParseEngine st = new BacktrackingParseEngine(globalParserPickerFactory);

		final TokenList t1 = TokenList.createD(Amount.valueOf(1, Unit.ONE), "+",
				Amount.valueOf(2, Unit.ONE), "*",
				Amount.valueOf(3, Unit.ONE));
		final ParserContext context = new ParserContext(st, Long.MAX_VALUE);

		final TokenList tokens = Tokenizer.tokenize("1.0 / ((401.0 / 0.06398498256905337) - 400.0)");
		final TokenList result = st.parseAndGetLastStep(tokens, context);
		System.out.println(result);
		System.out.println(Renderers.toHtml("", result));

		final TokenList r1 = st.parseAndGetLastStep(t1, context);
		Assert.assertEquals(Amount.valueOf(7, Unit.ONE), r1.get(0));

		final TokenList t2 = TokenList.createD(Amount.valueOf(1, Unit.ONE), "*",
				Amount.valueOf(2, Unit.ONE), "+", Amount.valueOf(3, Unit.ONE));
		final TokenList r2 = st.parseAndGetLastStep(t2, context);
		Assert.assertEquals(Amount.valueOf(5, Unit.ONE), r2.get(0));

	}

	// @Test
	public void userDefinedParsersTest() {
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		com.lastcalc.parsers.Parser.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new PreParser());
		priorityParsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final ParseEngine st = new BacktrackingParseEngine(new CombinedParserPickerFactory(priorityPPF, catchAllPPF));

		final ParserContext context = new ParserContext(st, Long.MAX_VALUE);
		final TokenList squareUDPtokens = st.parseAndGetLastStep(Tokenizer.tokenize("square X = X*X"), context);
		Assert.assertEquals(squareUDPtokens.toString() + " is of size 1", squareUDPtokens.size(), 1);
		Assert.assertTrue(squareUDPtokens.get(0) + " is a UserDefinedParser",
				squareUDPtokens.get(0) instanceof UserDefinedParser);
		final UserDefinedParser squareUDP = (UserDefinedParser) squareUDPtokens.get(0);
		Assert.assertEquals("Validate squareUDP template", TokenList.createD("square", Object.class),
				squareUDP.getTemplate());
		Assert.assertEquals("Validate squareUDP after", TokenList.createD("X", "*", "X"), squareUDP.after);
		priorityPPF.addParser(squareUDP);
		final TokenList quadUDPtokens = st.parseAndGetLastStep(Tokenizer.tokenize("quad X = square (square X)"),
				context);
		Assert.assertEquals(quadUDPtokens.toString() + " is of size 1", 1, quadUDPtokens.size());
		Assert.assertTrue(quadUDPtokens.get(0) + " is a UserDefinedParser",
				quadUDPtokens.get(0) instanceof UserDefinedParser);
		final UserDefinedParser quadUDP = (UserDefinedParser) quadUDPtokens.get(0);
		Assert.assertEquals("Validate quadUDP template", TokenList.createD("quad", Object.class),
				quadUDP.getTemplate());

		priorityPPF.addParser(quadUDP);
		final TokenList result = st.parseAndGetLastStep(Tokenizer.tokenize("quad 2"), context);
		Assert.assertEquals(result.toString() + " is of size 1", 1, result.size());
		Assert.assertEquals(16, ((org.jscience.mathematics.number.Number) result.get(0)).intValue());
	}

}
