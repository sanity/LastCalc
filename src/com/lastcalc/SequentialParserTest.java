package com.lastcalc;

import java.util.List;

import javax.measure.unit.Unit;

import junit.framework.Assert;

import org.jscience.physics.amount.Amount;
import org.junit.Test;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SequentialParserTest {
	@Test
	public void incrementTest() {
		final SequentialParser sp = SequentialParser.create();
		sp.parseNext("increment [] = []");
		sp.parseNext("increment [H ... T] = [H+1 ... increment T]");
		final TokenList inc = sp.parseNext("increment [1,2,3]");
		Assert.assertEquals(1, inc.size());
		Assert.assertTrue(inc.get(0) instanceof List);
		final List<Object> list = (List<Object>) inc.get(0);
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(2, ((Amount) list.get(0)).longValue(Unit.ONE));
		Assert.assertEquals(3, ((Amount) list.get(1)).longValue(Unit.ONE));
		Assert.assertEquals(4, ((Amount) list.get(2)).longValue(Unit.ONE));
	}

	@Test
	public void concatTest() {
		final SequentialParser sp = SequentialParser.create();
		sp.parseNext("concat [[]] = []");
		sp.parseNext("concat [[] ... R] = concat R");
		sp.parseNext("concat [[H ... T1] ... T2] = [H ... concat [T1 ... T2]]");
		final TokenList res = sp.parseNext("concat [[1, 2], [3, 4]]");
		Assert.assertEquals(1, res.size());
		Assert.assertTrue(res.get(0) instanceof List);
		final List<Object> list = (List<Object>) res.get(0);
		Assert.assertEquals(4, list.size());
		Assert.assertEquals(1, ((Number) list.get(0)).longValue());
		Assert.assertEquals(2, ((Number) list.get(1)).longValue());
		Assert.assertEquals(3, ((Number) list.get(2)).longValue());
		Assert.assertEquals(4, ((Number) list.get(3)).longValue());
	}

	@Test
	public void precedenceTest() {
		final SequentialParser sp = SequentialParser.create();
		Assert.assertEquals(((Amount) sp.parseNext("3+5*2").get(0)).getExactValue(), 13);
		Assert.assertEquals(((Amount) sp.parseNext("2*(6/3)").get(0)).getExactValue(), 4);

	}
}
