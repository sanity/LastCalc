package com.lastcalc.parsers.math;

import com.google.common.collect.Lists;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import org.jscience.mathematics.number.LargeInteger;

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
public class GCDLCMParser extends Parser{


    private static TokenList template = TokenList.createD(Lists.<Object> newArrayList("gcd", "lcm"),Number.class, Number.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }


    @Override
    public Parser.ParseResult parse(final TokenList tokens, final int templatePos) {

        final String op = (String) tokens.get(templatePos);
        final Integer input1 = ((LargeInteger)tokens.get(templatePos + 1)).intValue();
        final Integer input2 = ((LargeInteger)tokens.get(templatePos + 2)).intValue();


        if(op.equals("gcd")){

            Integer result=gcd_iter(input1,input2);

            return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
        }
        else if(op.equals("lcm")){

            Integer result=lcm(input1,input2);

            return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
        }
        else{
            return Parser.ParseResult.fail();
        }
    }

    private Integer gcd_iter(Integer u, Integer v) {
        Integer t;
        while (v!=0) {
            t = u;
            u = v;
            v = t % v;
        }
        return u < 0 ? -u : u; /* abs(u) */
    }

    private Integer lcm(Integer a, Integer b)
    {
        return a * (b / gcd_iter(a, b));
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
