package com.lastcalc.parsers.bool;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ian on 6/3/15.
 */
public class IfThenElseParserTest {
    @Test
    public void ifThenElseTrueTest() {
        IfThenElseParser ifThenElseParser = new IfThenElseParser();
        TokenList trueTL = TokenList.createD("(", "if", Boolean.TRUE, "then", 1, "else", 0, ")");
        Parser.ParseResult trueResult = ifThenElseParser.parse(trueTL, 1);
        Assert.assertTrue(trueResult.isSuccess());
        Assert.assertEquals(TokenList.createD("(", 1, ")"), trueResult.output);

        TokenList falseTL = TokenList.createD("(", "if", Boolean.FALSE, "then", 1, "else", 0, ")");
        Parser.ParseResult falseResult = ifThenElseParser.parse(falseTL, 1);
        Assert.assertTrue(falseResult.isSuccess());
        Assert.assertEquals(TokenList.createD("(", 0, ")"), falseResult.output);
    }

    @Test
    public void ifThenTest() {
        IfThenElseParser ifThenElseParser = new IfThenElseParser();
        TokenList trueTL = TokenList.createD("(", "if", Boolean.TRUE, "then", 1, ")");
        Parser.ParseResult trueResult = ifThenElseParser.parse(trueTL, 1);
        Assert.assertTrue(trueResult.isSuccess());
        Assert.assertEquals(TokenList.createD("(", 1, ")"), trueResult.output);

        TokenList falseTL = TokenList.createD("(", "if", Boolean.FALSE, "then", 1, ")");
        Parser.ParseResult falseResult = ifThenElseParser.parse(falseTL, 1);
        Assert.assertFalse(falseResult.isSuccess());
    }
}