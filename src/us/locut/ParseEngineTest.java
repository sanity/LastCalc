package us.locut;

import java.util.LinkedList;

import org.junit.Test;

import us.locut.engines.*;
import us.locut.parsers.*;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class ParseEngineTest {

	@Test
	public void test() {

		final LinkedList<Parser> parsers = Lists.newLinkedList();
		Parsers.getAll(parsers);
		final RecentFirstParserPickerFactory rfppf = new RecentFirstParserPickerFactory(parsers);
		final ParseEngine st = new BacktrackingParseEngine(rfppf);
		// final ParseEngine st = new SimpleParseEngine(rfppf);

		final ParserContext context = new ParserContext(st, System.currentTimeMillis() + 2000);
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
