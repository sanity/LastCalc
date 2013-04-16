package com.lastcalc.parsers.math;

/**
 * Created with IntelliJ IDEA.
 * User: olee
 * Date: 4/7/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArithBiOpExpression implements ArithExpression {

    final ArithExpression lhs;
    final ArithExpression rhs;
    final Op op;


    enum Op{
        PLUS, MINUS, MULT, DIV, MOD, POWER, LOG, ;


    }

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

            switch(op){

                case PLUS:

                    break;
                case MINUS:
            }


        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
