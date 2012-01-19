package com.lastcalc.parsers.collections;

import java.util.LinkedList;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import org.junit.Test;

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
