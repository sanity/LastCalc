package us.locut.parsers.amounts;

import java.util.Set;

import org.junit.Test;

public class UnitParserTest {

	@Test
	public void test() {
		final Set<UnitParser> parsers = UnitParser.getParsers();
		System.out.println(parsers);
	}

}
