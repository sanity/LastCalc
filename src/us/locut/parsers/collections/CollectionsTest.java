package us.locut.parsers.collections;

import java.util.LinkedList;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import org.junit.Test;

import us.locut.*;
import us.locut.engines.*;
import us.locut.parsers.*;
import us.locut.parsers.amounts.AmountMathOp;

public class CollectionsTest {

	@Test
	public void getFromMapTest() {
		final PreParser bp = new PreParser();
		final TokenList origTokens = Parsers.tokenize("get b from {a:A, b:B}");
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
		final TokenList result = st.parseAndGetLastStep(origTokens, context);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("B", result.get(0));
	}
}
