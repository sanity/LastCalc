package us.locut;

import java.util.*;

import javax.measure.unit.Unit;

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
	public void operatorPrecidenceTest() {
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		us.locut.Parsers.getAll(parsers);
		final LinkedList<Parser> priorityParsers = Lists.newLinkedList();
		priorityParsers.add(new PreParser());
		priorityParsers.addAll(AmountMathOp.getOps());
		parsers.add(new UserDefinedParserParser());
		final FixedOrderParserPickerFactory priorityPPF = new FixedOrderParserPickerFactory(priorityParsers);
		final RecentFirstParserPickerFactory catchAllPPF = new RecentFirstParserPickerFactory(parsers);
		final CombinedParserPickerFactory globalParserPickerFactory = new CombinedParserPickerFactory(priorityPPF,
				catchAllPPF);
		final ParseEngine st = new BacktrackingParseEngine(globalParserPickerFactory);

		final List<Object> t1 = Lists.<Object> newArrayList(Amount.valueOf(1, Unit.ONE), "+",
				Amount.valueOf(2, Unit.ONE), "*",
				Amount.valueOf(3, Unit.ONE));
		final ParserContext context = new ParserContext(st, Long.MAX_VALUE);

		final ArrayList<Object> tokens = Parsers.tokenize("1.0 / ((401.0 / 0.06398498256905337) - 400.0)");
		final List<Object> result = st.parseAndGetLastStep(tokens, context);
		System.out.println(result);
		System.out.println(Renderers.toHtml("", result));

		final List<Object> r1 = st.parseAndGetLastStep(t1, context);
		Assert.assertEquals(Amount.valueOf(7, Unit.ONE), r1.get(0));

		final List<Object> t2 = Lists.<Object> newArrayList(Amount.valueOf(1, Unit.ONE), "*",
				Amount.valueOf(2, Unit.ONE), "+", Amount.valueOf(3, Unit.ONE));
		final List<Object> r2 = st.parseAndGetLastStep(t2, context);
		Assert.assertEquals(Amount.valueOf(5, Unit.ONE), r2.get(0));

	}

	@Test
	public void userDefinedParsersTest() {
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
		final List<Object> squareUDPtokens = st.parseAndGetLastStep(Parsers.tokenize("square X = X*X"), context);
		Assert.assertEquals(squareUDPtokens.toString() + " is of size 1", squareUDPtokens.size(), 1);
		Assert.assertTrue(squareUDPtokens.get(0) + " is a UserDefinedParser",
				squareUDPtokens.get(0) instanceof UserDefinedParser);
		final UserDefinedParser squareUDP = (UserDefinedParser) squareUDPtokens.get(0);
		Assert.assertEquals("Validate squareUDP template", Lists.newArrayList("square", Object.class),
				squareUDP.getTemplate());
		Assert.assertEquals("Validate squareUDP after", Lists.newArrayList("X", "*", "X"), squareUDP.after);
		priorityPPF.addParser(squareUDP);
		final List<Object> quadUDPtokens = st.parseAndGetLastStep(Parsers.tokenize("quad X = square (square X)"),
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
