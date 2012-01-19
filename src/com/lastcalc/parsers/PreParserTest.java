package com.lastcalc.parsers;

import junit.framework.Assert;

import org.junit.Test;

import com.lastcalc.*;

public class PreParserTest {
	@Test
	public void findObjectTest() {
		final TokenList tl = Tokenizer.tokenize("i wish (i was) a tea pot");
		Assert.assertEquals(tl.indexOf("wish"), PreParser.findEdgeOrObjectForwards(tl, tl.indexOf("i"), "wish"));
		Assert.assertEquals(tl.size() - 1, PreParser.findEdgeOrObjectForwards(tl, tl.indexOf("i"), "i"));
		Assert.assertEquals(0, PreParser.findEdgeOrObjectBackwards(tl, tl.indexOf("pot"), "i"));
		Assert.assertEquals(tl.indexOf("(") + 1, PreParser.findEdgeOrObjectBackwards(tl, tl.indexOf("was"), null));
		Assert.assertEquals(tl.size() - 1, PreParser.findEdgeOrObjectForwards(tl, tl.indexOf("wish"), null));
	}
}
