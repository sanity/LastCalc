package us.locut;

import java.util.*;

import org.junit.Test;

import us.locut.engines.*;
import us.locut.parsers.Parser;
import us.locut.parsers.amounts.*;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class SimpleTransformerTest {

	@Test
	public void test() {

		final LinkedList<Parser> parsers = Lists.newLinkedList();
		parsers.addAll(UnitParser.getParsers());
		parsers.add(new AmountParser());
		parsers.addAll(AmountMathOp.getOps());
		final ParseEngine st = new SimpleParseEngine(parsers);
		final ArrayList<Object> res = st.parse(Parsers.tokenize("30 miles / 5 minutes"), 1000);
		System.out.println(res);
	}

}
