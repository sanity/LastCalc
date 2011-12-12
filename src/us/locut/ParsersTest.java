package us.locut;

import java.util.ArrayList;

import org.junit.Test;

public class ParsersTest {

	@Test
	public void test() {
		final String test1 = "one=15+(3/7.8)*12,533 + average of [1, 2, 3]";
		final ArrayList<Object> test1t = Parsers.tokenize(test1);

	}

}
