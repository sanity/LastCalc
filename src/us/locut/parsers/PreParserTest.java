package us.locut.parsers;

import java.util.*;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import org.junit.Test;

import us.locut.Parsers;
import us.locut.engines.*;
import us.locut.parsers.PreParser.SubTokenSequence;
import us.locut.parsers.amounts.AmountMathOp;

public class PreParserTest {

	@Test
	public void bracketsParseTest() {
		final PreParser bp = new PreParser();
		final List<Object> origTokens = Lists.<Object> newArrayList("a", "(", "b", "c", ")", "d");
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
		final List<Object> parsedTokens = bp.parse(origTokens, origTokens.indexOf(")"), context).output;
		Assert.assertEquals(3, parsedTokens.size());
		Assert.assertEquals("a", parsedTokens.get(0));
		Assert.assertEquals("d", parsedTokens.get(2));
		Assert.assertTrue(parsedTokens.get(1) instanceof PreParser.SubTokenSequence);
		final SubTokenSequence subSeq = (SubTokenSequence) parsedTokens.get(1);
		Assert.assertEquals(2, subSeq.tokens.size());
		Assert.assertEquals("b", subSeq.tokens.get(0));
		Assert.assertEquals("c", subSeq.tokens.get(1));
		final List<Object> flattenedTokens = PreParser.flatten(parsedTokens);
		Assert.assertEquals(origTokens, flattenedTokens);
	}

	@Test
	public void listParseTest() {
		final PreParser bp = new PreParser();
		final List<Object> origTokens = Lists.<Object> newArrayList("a", "[", "b", "e", ",", "n", "]", "d");
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
		final List<Object> parsedTokens = bp.parse(origTokens, origTokens.indexOf("]"), context).output;
		Assert.assertEquals(3, parsedTokens.size());
		Assert.assertEquals("a", parsedTokens.get(0));
		Assert.assertEquals("d", parsedTokens.get(2));
		Assert.assertTrue(parsedTokens.get(1) instanceof List);
		final List<Object> list = (List<Object>) parsedTokens.get(1);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.get(0) instanceof SubTokenSequence);
		final SubTokenSequence seq = (SubTokenSequence) list.get(0);
		Assert.assertEquals(2, seq.tokens.size());
		Assert.assertEquals("b", seq.tokens.get(0));
		Assert.assertEquals("e", seq.tokens.get(1));
		Assert.assertEquals("n", list.get(1));
		final List<Object> flattened = PreParser.flatten(parsedTokens);
		System.out.println(origTokens);
		System.out.println(flattened);
		Assert.assertEquals(origTokens, flattened);
	}

	@Test
	public void mapParseTest() {
		final PreParser bp = new PreParser();
		final List<Object> origTokens = Parsers.tokenize("a b {one : o n, two : t, three : k} c");
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
		final List<Object> parsedTokens = bp.parse(origTokens, origTokens.indexOf("}"), context).output;
		Assert.assertEquals(4, parsedTokens.size());
		Assert.assertEquals("a", parsedTokens.get(0));
		Assert.assertEquals("b", parsedTokens.get(1));
		Assert.assertEquals("c", parsedTokens.get(3));
		Assert.assertTrue(parsedTokens.get(2) instanceof Map);
		final Map<Object, Object> map = (Map<Object, Object>) parsedTokens.get(2);
		Assert.assertEquals(3, map.size());
		Assert.assertTrue(map.get("one") instanceof SubTokenSequence);
		final SubTokenSequence seq = (SubTokenSequence) map.get("one");
		Assert.assertEquals(2, seq.tokens.size());
		Assert.assertEquals("o", seq.tokens.get(0));
		Assert.assertEquals("n", seq.tokens.get(1));
		Assert.assertEquals(map.get("two"), "t");
		Assert.assertEquals(map.get("three"), "k");
		final List<Object> flattened = PreParser.flatten(parsedTokens);
		Assert.assertEquals(origTokens, flattened);
	}

	@Test
	public void combinedTest() {
		final List<Object> origTokens = Parsers.tokenize("{[a, b] : ab, [d, e] : [f, g]}");
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
		final List<Object> parsedTokens = st.parseAndGetLastStep(origTokens, context);
		final List<Object> flattened = PreParser.flatten(parsedTokens);
		System.out.println(origTokens);
		System.out.println(flattened);
		Assert.assertEquals(origTokens, flattened);
	}
}
