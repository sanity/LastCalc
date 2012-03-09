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
package com.lastcalc;

import org.junit.Test;

public class ParsersTest {

	@Test
	public void testTokenize() {
		final String test1 = "one=15+(3/7.8)*12,533 + average of [1, 2...[3]]";
		final TokenList test1t = Tokenizer.tokenize(test1);
		for (final Object o : test1t) {
			System.out.println(o);
		}
	}
}
