package us.locut.parsers;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import us.locut.parsers.Parser.ParseResult;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class ReducerTest {

	@Test
	public void testCreateResponse() {
		final Parser simple = new Parser() {
			private static final long serialVersionUID = -1132507808469916351L;

			@Override
			public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
				return ParseResult.success(createResponse(tokens, templatePos, "c"), "Simple Reducer");
			}

			@Override
			public ArrayList<Object> getTemplate() {
				return Lists.<Object> newArrayList("d", "e");
			}

			@Override
			public int hashCode() {
				return 243869245;
			}

			@Override
			public boolean equals(final Object obj) {
				return obj == this;
			}
		};

		final ParseResult reduced = simple.parse(Lists.<Object> newArrayList("a", "b", "c", "d", "e", "f", "g"), 3);

		final ArrayList<Object> correct = Lists.<Object> newArrayList("a", "b", "c", "c", "f", "g");

		Assert.assertEquals(correct, reduced.output);
	}
}