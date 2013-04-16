package com.lastcalc.parsers.math;

/**
 * Created with IntelliJ IDEA.
 * User: olee
 * Date: 4/7/13
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariableExpression implements ArithExpression {               //primitive, atomic, variable expression, just a single variable

    final String name;

    public VariableExpression(String name) {
        this.name = name;
    }


    @Override
    public ArithExpression simplify() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
