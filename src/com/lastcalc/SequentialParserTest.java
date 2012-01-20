package com.lastcalc;

import java.util.List;

import junit.framework.Assert;

import org.jscience.mathematics.number.LargeInteger;
import org.junit.Test;

import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SequentialParserTest {

	@Test
	public void parseUDPAnswerTest() {
		final SequentialParser sp = SequentialParser.create();
		final TokenList res = sp.parseNext("a=2+3");
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.get(0) instanceof UserDefinedParser);
		final UserDefinedParser udp = (UserDefinedParser) res.get(0);
		Assert.assertEquals(1, udp.after.size());
	}

	@Test
	public void parseWithPrevAnswer() {
		final SequentialParser sp = SequentialParser.create();
		sp.parseNext("49+3");
		final TokenList res = sp.parseNext("+1");
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.get(0) instanceof LargeInteger);
		Assert.assertEquals(53, ((LargeInteger) res.get(0)).intValue());
	}

	@Test
	public void incrementTest() {
		final SequentialParser sp = SequentialParser.create();
		sp.parseNext("increment [] = []");
		sp.parseNext("increment [H ... T] = [H+1 ... increment T]");
		// sp.setDumpSteps(true);
		final TokenList inc = sp.parseNext("increment [1,2,3,4,5,6,7,8]");
		sp.setDumpSteps(false);
		System.out.println("Increment steps required: \t" + sp.getLastParseStepCount());
		Assert.assertTrue(sp.getLastParseStepCount() <= 35);
		Assert.assertEquals("Expected [2,3,4,5,6,7,8,9] but was " + inc, 1, inc.size());
		Assert.assertTrue(inc.get(0) instanceof List);
		final List<Object> list = (List<Object>) inc.get(0);
		Assert.assertEquals(8, list.size());
		Assert.assertEquals(2, ((LargeInteger) list.get(0)).intValue());
		Assert.assertEquals(3, ((LargeInteger) list.get(1)).intValue());
		Assert.assertEquals(4, ((LargeInteger) list.get(2)).intValue());
		Assert.assertEquals(5, ((LargeInteger) list.get(3)).intValue());
		Assert.assertEquals(6, ((LargeInteger) list.get(4)).intValue());
		Assert.assertEquals(7, ((LargeInteger) list.get(5)).intValue());
		Assert.assertEquals(8, ((LargeInteger) list.get(6)).intValue());
		Assert.assertEquals(9, ((LargeInteger) list.get(7)).intValue());
	}

	@Test
	public void filterTest() {
		final SequentialParser sp = SequentialParser.create();
		sp.parseNext("aboveFive [] = []");
		sp.parseNext("aboveFive [H ... T] = if H > 5 then [H ... aboveFive T] else aboveFive T");
		final TokenList inc = sp.parseNext("aboveFive [4,5,6,7,8,9,10,11,12]");
		System.out.println("Filter steps required: \t" + sp.getLastParseStepCount());
		Assert.assertTrue(sp.getLastParseStepCount() <= 106);
		Assert.assertEquals("Expected [6,7] but was " + inc, 1, inc.size());
		Assert.assertTrue(inc.get(0) instanceof List);
		final List<Object> list = (List<Object>) inc.get(0);
		Assert.assertEquals(7, list.size());
		Assert.assertEquals(6, ((LargeInteger) list.get(0)).intValue());
		Assert.assertEquals(7, ((LargeInteger) list.get(1)).intValue());
		Assert.assertEquals(8, ((LargeInteger) list.get(2)).intValue());
		Assert.assertEquals(9, ((LargeInteger) list.get(3)).intValue());
		Assert.assertEquals(10, ((LargeInteger) list.get(4)).intValue());
		Assert.assertEquals(11, ((LargeInteger) list.get(5)).intValue());
		Assert.assertEquals(12, ((LargeInteger) list.get(6)).intValue());
	}

	@Test
	public void concatTest() {
		final SequentialParser sp = SequentialParser.create();
		sp.parseNext("concat [[]] = []");
		sp.parseNext("concat [[] ... R] = concat R");
		sp.parseNext("concat [[H ... T1] ... T2] = [H ... concat [T1 ... T2]]");
		final TokenList res = sp.parseNext("concat [[1, 2], [3, 4]]");
		System.out.println("Concat steps required: \t" + sp.getLastParseStepCount());
		Assert.assertTrue(sp.getLastParseStepCount() <= 30);
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.get(0) instanceof List);
		final List<Object> list = (List<Object>) res.get(0);
		Assert.assertEquals(4, list.size());
		Assert.assertEquals(1, ((LargeInteger) list.get(0)).intValue());
		Assert.assertEquals(2, ((LargeInteger) list.get(1)).intValue());
		Assert.assertEquals(3, ((LargeInteger) list.get(2)).intValue());
		Assert.assertEquals(4, ((LargeInteger) list.get(3)).intValue());
	}

	@Test
	public void precedenceTest() {
		final SequentialParser sp = SequentialParser.create();
		Assert.assertEquals(((org.jscience.mathematics.number.Number) sp.parseNext("3+5*2").get(0)).intValue(), 13);
		Assert.assertEquals(((org.jscience.mathematics.number.Number) sp.parseNext("2*(6/3)").get(0)).intValue(), 4);
	}
}
