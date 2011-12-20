package us.locut.parsers.collections;

import java.util.*;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import org.jscience.physics.amount.Amount;
import org.junit.Test;

import us.locut.Parsers;
import us.locut.engines.*;
import us.locut.parsers.*;
import us.locut.parsers.amounts.AmountMathOp;

public class CollectionsTest {

	@Test
	public void getFromMapTest() {
		final PreParser bp = new PreParser();
		final List<Object> origTokens = Parsers.tokenize("get b from {a:A, b:B}");
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		us.locut.Parsers.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new PreParser());
		priorityParsers.addAll(AmountMathOp.getOps());
		priorityParsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final ParseEngine st = new BacktrackingParseEngine(new CombinedParserPickerFactory(priorityPPF, catchAllPPF));
		final ParserContext context = new ParserContext(st, Long.MAX_VALUE);
		final List<Object> result = st.parseAndGetLastStep(origTokens, context);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("B", result.get(0));
	}

	@Test
	public void mapToTest() {
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		us.locut.Parsers.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new PreParser());
		priorityParsers.addAll(AmountMathOp.getOps());
		priorityParsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final ParseEngine st = new BacktrackingParseEngine(new CombinedParserPickerFactory(priorityPPF, catchAllPPF));
		final ParserContext context = new ParserContext(st, 10000);
		final List<Object> origTokens = Parsers.tokenize("map (x=x+x) to [1,2,3,4]");
		final List<Object> parsed = st.parseAndGetLastStep(origTokens, context);
		Assert.assertEquals(1, parsed.size());
		Assert.assertTrue(parsed.get(0) instanceof List);
		final List<Object> list = (List<Object>) parsed.get(0);
		Assert.assertEquals(4, list.size());
		Assert.assertEquals(((Amount<?>) list.get(0)).getExactValue(), 2);
		Assert.assertEquals(((Amount<?>) list.get(1)).getExactValue(), 4);
		Assert.assertEquals(((Amount<?>) list.get(2)).getExactValue(), 6);
		Assert.assertEquals(((Amount<?>) list.get(3)).getExactValue(), 8);

	}
}
