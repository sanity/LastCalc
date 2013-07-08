package com.lastcalc.parsers.math;

/**
 * Created with IntelliJ IDEA.
 * User: olee
 * Date: 4/7/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstantExpression implements ArithExpression {   //primitive constant expression, just a single number

    final Number num;

    public ConstantExpression(Number num) {
        this.num = num;
    }


    @Override
    public ArithExpression simplify() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
