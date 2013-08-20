package com.lastcalc.parsers.math;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import junit.framework.Assert;
import org.jscience.mathematics.number.LargeInteger;
import org.junit.Test;

/**
 * File created by TRACLabs
 * User: oliverl3
 * Date: 8/14/13
 */
public class AlgebraTests {




    @Test
    public void basicAdditionConstantExpressionSimplify(){

        
        int left=3;
        int right=4;
        
        ArithBiOpExpression.Op operation= ArithBiOpExpression.Op.PLUS;
        
        final ArithBiOpExpression expressionParser=new ArithBiOpExpression(new ConstantExpression(left), new ConstantExpression(right), operation);
        
        ConstantExpression answer=(ConstantExpression)expressionParser.simplify();
        
        Assert.assertEquals(answer.num.intValue(),7);
        
        
        
    }
    
    
    
}
