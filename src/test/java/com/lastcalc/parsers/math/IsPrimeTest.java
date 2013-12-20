package com.lastcalc.parsers.math;

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
public class IsPrimeTest {



    @Test
    public void isPrimeTest(){

        final IsPrimeParser ist = new IsPrimeParser();
        Parser.ParseResult pr=ist.parse(TokenList.createD("blah", "isprime", LargeInteger.valueOf(3), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", true,"blah"), pr.output);

        pr=ist.parse(TokenList.createD("blah", "isprime", LargeInteger.valueOf(5), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", true,"blah"), pr.output);


        pr=ist.parse(TokenList.createD("blah", "isprime", LargeInteger.valueOf(10), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", false,"blah"), pr.output);

        pr=ist.parse(TokenList.createD("blah", "isprime", LargeInteger.valueOf(29), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", true,"blah"), pr.output);

        pr=ist.parse(TokenList.createD("blah", "isprime", LargeInteger.valueOf(100), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", false,"blah"), pr.output);

        pr=ist.parse(TokenList.createD("blah", "isprime", LargeInteger.valueOf(7919 ), "blah"),1);
        Assert.assertTrue("Ensure parse was successful: ", pr.isSuccess());
        Assert.assertEquals("Ensure parse result is what it's supposed to be: ",TokenList.createD("blah", true,"blah"), pr.output);



    }
}
