package us.locut;

import java.util.*;

import junit.framework.Assert;

import org.junit.Test;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

public class ParsersTest {

	@Test
	public void testTokenize() {
		final String test1 = "one=15+(3/7.8)*12,533 + average of [1, 2, 3]";
		final ArrayList<Object> test1t = Parsers.tokenize(test1);

	}

	@Test
	public void parseQuestionTest() {
		final Map<String, ArrayList<Object>> variables = Maps.newHashMap();
		variables.put("vname1", Parsers.tokenize("vval1"));
		final String test1 = "vname2 is 5 +6 / vname1";
		final ParsedQuestion pq = Parsers.parseQuestion(test1, variables);
		Assert.assertEquals("vname2", pq.variableAssignment);
		Assert.assertEquals(Parsers.tokenize("5 +6 / vval1"), pq.question);
	}

}
