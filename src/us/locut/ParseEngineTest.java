package us.locut;

import java.util.*;

import junit.framework.Assert;

import com.google.common.collect.Lists;

import org.jscience.physics.amount.Amount;
import org.junit.Test;

import us.locut.engines.*;
import us.locut.parsers.*;
import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;
import us.locut.parsers.amounts.AmountMathOp;

public class ParseEngineTest {

	@Test
	public void userDefinedParsersTest() {

		final LinkedList<Parser> parsers = Lists.newLinkedList();
		us.locut.Parsers.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new BracketsParser());
		priorityParsers.addAll(AmountMathOp.getOps());
		priorityParsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final ParseEngine st = new BacktrackingParseEngine(new CombinedParserPickerFactory(priorityPPF, catchAllPPF));

		final ParserContext context = new ParserContext(st, Long.MAX_VALUE);
		final List<Object> squareUDPtokens = st.parseAndGetLastStep(Parsers.tokenize("square x = x*x"), context);
		Assert.assertEquals(squareUDPtokens.toString() + " is of size 1", squareUDPtokens.size(), 1);
		Assert.assertTrue(squareUDPtokens.get(0) + " is a UserDefinedParser",
				squareUDPtokens.get(0) instanceof UserDefinedParser);
		final UserDefinedParser squareUDP = (UserDefinedParser) squareUDPtokens.get(0);
		Assert.assertEquals("Validate squareUDP template", Lists.newArrayList("square", Object.class),
				squareUDP.getTemplate());
		Assert.assertEquals("Validate squareUDP after", Lists.newArrayList("x", "*", "x"), squareUDP.after);
		priorityPPF.addParser(squareUDP);
		final List<Object> quadUDPtokens = st.parseAndGetLastStep(Parsers.tokenize("quad x = square (square x)"),
				context);
		Assert.assertEquals(quadUDPtokens.toString() + " is of size 1", 1, quadUDPtokens.size());
		Assert.assertTrue(quadUDPtokens.get(0) + " is a UserDefinedParser",
				quadUDPtokens.get(0) instanceof UserDefinedParser);
		final UserDefinedParser quadUDP = (UserDefinedParser) quadUDPtokens.get(0);
		Assert.assertEquals("Validate quadUDP template", Lists.newArrayList("quad", Object.class),
				quadUDP.getTemplate());

		priorityPPF.addParser(quadUDP);
		final List<Object> result = st.parseAndGetLastStep(Parsers.tokenize("quad 2"), context);
		Assert.assertEquals(result.toString() + " is of size 1", 1, result.size());
		Assert.assertEquals(16, ((Amount<?>) result.get(0)).getExactValue());
	}

}
