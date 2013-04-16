package com.lastcalc.parsers.math;

/**
 * Created with IntelliJ IDEA.
 * User: olee
 * Date: 4/7/13
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Equation {

    ArithExpression lhs;
    ArithExpression rhs;
    Comparator comparator;

    enum Comparator{
        EQUALS, NOTEQUALS, LESSTHANEQUALS, LESSTHAN, GREATERTHANEQUALS,GREATERTHAN;

    }




}
