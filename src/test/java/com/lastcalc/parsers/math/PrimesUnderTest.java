package com.lastcalc.parsers.math;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import junit.framework.Assert;
import org.jscience.mathematics.number.LargeInteger;
import org.junit.Test;

import java.util.ArrayList;

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
public class PrimesUnderTest {



    @Test
    public void primesUnderTest(){

        final PrimesUnderParser pup = new PrimesUnderParser();
        Parser.ParseResult pr=pup.parse(TokenList.createD("blah", "primesunder", LargeInteger.valueOf(3), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", Lists.newArrayList(2,3),"blah"), pr.output);
    }
}
