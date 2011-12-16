package us.locut.parsers;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import us.locut.Parsers;
import us.locut.parsers.Parser.ParseResult;
import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;

public class UserDefinedParserParserTest {

	@Test
	public void test() {
		final UserDefinedParserParser udpp = new UserDefinedParserParser();

		final List<Object> tokens = Parsers.tokenize("X squared = X * X");
		final ParseResult result = udpp.parse(tokens, tokens.indexOf("="), null);

		final UserDefinedParserParser.UserDefinedParser udp = (UserDefinedParser) result.output.get(0);

		final List<Object> input = Parsers.tokenize("15 squared");

		final ParseResult parseResult2 = udp.parse(input, 0);

		Assert.assertEquals(Parsers.tokenize("15 * 15"), parseResult2.output);
	}

}
