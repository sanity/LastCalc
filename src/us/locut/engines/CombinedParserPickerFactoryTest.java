package us.locut.engines;

import java.util.List;

import org.junit.*;

import us.locut.parsers.*;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class CombinedParserPickerFactoryTest {

	@Test
	public void test() {
		final Parser p1 = new RewriteParser(Lists.newArrayList("1", "1"), "one");
		final Parser p2 = new RewriteParser(Lists.newArrayList("2", "2"), "two");
		final Parser p3 = new RewriteParser(Lists.newArrayList("3", "3"), "three");
		final Parser p4 = new RewriteParser(Lists.newArrayList("4", "4"), "four");

		final FixedOrderParserPickerFactory f1 = new FixedOrderParserPickerFactory(p1, p2);
		final FixedOrderParserPickerFactory f2 = new FixedOrderParserPickerFactory(p3, p4);
		final CombinedParserPickerFactory f = new CombinedParserPickerFactory(f1, f2);

		final BacktrackingParseEngine pe = new BacktrackingParseEngine(f);

		final ParserContext context = new ParserContext(pe, Long.MAX_VALUE);
		final List<Object> result = pe.parseAndGetLastStep(
				Lists.<Object> newArrayList("1", "1", "2", "2", "3", "3", "4", "4"),
				context);

		Assert.assertEquals(Lists.<Object> newArrayList("one", "two", "three", "four"), result);
	}

}
