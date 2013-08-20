package com.lastcalc.parsers.math;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: olee
 * Date: 4/7/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArithBiOpExpression extends Parser implements ArithExpression {

    final private ArithExpression lhs;
    final private ArithExpression rhs;
    final private Op op;

    enum Op{
        PLUS, MINUS, MULT, DIV, MOD, POWER, LOG, ;
    }



    private static Map<Op, Integer> precidenceMap = Maps.newHashMap();
    static {
        precidenceMap.put(Op.POWER, 2);
        precidenceMap.put(Op.LOG, 2);
        precidenceMap.put(Op.MULT, 3);
        precidenceMap.put(Op.DIV, 3);
        precidenceMap.put(Op.MOD, 3);
        precidenceMap.put(Op.PLUS, 4);
        precidenceMap.put(Op.MINUS, 4);
    }


    

    private static TokenList template = TokenList.createD(
            Object.class,
            Lists.<Object> newArrayList(precidenceMap.keySet()),
            Object.class);

    


    
    
    

    public ArithBiOpExpression(ArithExpression lhs, ArithExpression rhs, Op op) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
    }

    @Override
    public ArithExpression simplify() {

        ArithExpression simplifiedLHS=lhs.simplify();
        ArithExpression simplifiedRHS=rhs.simplify();

        if(simplifiedLHS instanceof ConstantExpression && simplifiedRHS instanceof ConstantExpression){
            
            Number left=((ConstantExpression)simplifiedLHS).num;
            Number right=((ConstantExpression)simplifiedRHS).num;

            
            switch(op){

                case PLUS:
                    return new ConstantExpression(left.intValue()+right.intValue());
                case MINUS:
                    break;
                case MULT:
                    break;
                case DIV:
                    break;
                case MOD:
                    break;
                case POWER:
                    break;
                case LOG:
                    break;
            }


        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    @SuppressWarnings("rawtypes")
    @Override
    public ParseResult parse(final TokenList tokens, final int templatePos) {
        
        
        return null;
    }



    @Override
    public TokenList getTemplate() {

        return template;
    }

    @Override
    public int hashCode() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
