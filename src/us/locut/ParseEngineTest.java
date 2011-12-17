package us.locut;

import java.util.LinkedList;

import com.google.common.collect.Lists;

import org.junit.Test;

import us.locut.engines.*;
import us.locut.parsers.*;
import us.locut.parsers.amounts.AmountMathOp;

public class ParseEngineTest {

	@Test
	public void test() {

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
		final LinkedList<ParseStep> res = st.parse(Parsers.tokenize("2 miles *5"),
				context);
		for (final ParseStep ps : res) {
			System.out.println(ps);
		}

		// res = st.parse(Parsers.tokenize("30 miles [1,2,3,4]"), context);
		// for (final ParseStep ps : res) {
		// System.out.println(ps);
		// }
	}

}
