package us.locut.reducers;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import us.locut.reducers.Parser.ParseResult;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class ReducerTest {

	@Test
	public void testCreateResponse() {
		final Parser simple = new Parser() {

			@Override
			public ParseResult reduce(final ArrayList<Object> tokens, final int templatePos) {
				return new ParseResult(createResponse(tokens, templatePos, "c"), "Simple Reducer");
			}

			@Override
			public ArrayList<Object> getTemplate() {
				return Lists.<Object> newArrayList("d", "e");
			}
		};

		final ParseResult reduced = simple.reduce(Lists.<Object> newArrayList("a", "b", "c", "d", "e", "f", "g"), 3);

		final ArrayList<Object> correct = Lists.<Object> newArrayList("a", "b", "c", "c", "f", "g");

		Assert.assertEquals(correct, reduced.output);
	}
}