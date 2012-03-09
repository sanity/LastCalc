/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
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
