package com.lastcalc.parsers;

import junit.framework.Assert;

import org.jscience.mathematics.number.LargeInteger;
import org.junit.Test;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser.ParseResult;

public class FactorialParserTest {

	@Test
	public void successfulParseTest(){
		
		final FactorialParser fp = new FactorialParser();
		ParseResult pr=fp.parse(TokenList.createD("blah","factorial",LargeInteger.valueOf(3),"blah"),1);
		Assert.assertTrue("Ensure parse was succcesful", pr.isSuccess());
		Assert.assertEquals("Ensure parse result is what it's supposed to be",TokenList.createD("blah",LargeInteger.valueOf(6),"blah"), pr.output);
	}
	
	
	@Test
	public void tooBigOrSmallTest(){
		
		final FactorialParser fp = new FactorialParser();
		ParseResult pr=fp.parse(TokenList.createD("blah","factorial",LargeInteger.valueOf(-1),"blah"),1);
		Assert.assertFalse("Ensure parse failed b/c it's a negative num", pr.isSuccess());

		ParseResult pr2=fp.parse(TokenList.createD("blah","factorial",LargeInteger.valueOf(20),"blah"),1);
		Assert.assertFalse("Ensure parse failed b/c it's a too big", pr2.isSuccess());
		
	}
	
	
	
}
