package com.lastcalc.parsers.math;

/**
 * Created with IntelliJ IDEA.
 * User: olee
 * Date: 4/7/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Expression <T extends Expression>{


    public T simplify();
}
