package com.lastcalc.parsers.math;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import junit.framework.Assert;
import org.jscience.mathematics.number.LargeInteger;
import org.junit.Test;

/**
 * ****************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * <p/>
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU Affero General Public License for more
 * details.
 * ****************************************************************************
 */
public class GCDTest {


    @Test
    public void gcdTest(){

        final GCDParser gcdParser = new GCDParser();
        Parser.ParseResult pr=gcdParser.parse(TokenList.createD("blah", "gcd", LargeInteger.valueOf(3), LargeInteger.valueOf(4), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", 1,"blah"), pr.output);


    }
}
