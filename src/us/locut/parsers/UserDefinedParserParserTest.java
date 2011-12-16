package us.locut.parsers;

import java.util.List;

import org.junit.Test;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class UserDefinedParserParserTest {

	@Test
	public void test() {
		final UserDefinedParserParser udpp = new UserDefinedParserParser();

		final List<Object> tokens = Lists.<Object> newArrayList(1, 2, "(", 3, "(", "5", ")", "8", "=", "(", 9,
				")", ")", 10);

		udpp.parse(tokens, tokens.indexOf("="), null);
	}

}
