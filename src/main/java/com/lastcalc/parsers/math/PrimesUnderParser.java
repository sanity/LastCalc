package com.lastcalc.parsers.math;

import com.google.common.collect.Lists;
import com.lastcalc.TokenList;
import com.lastcalc.bootstrap.Bootstrap;
import com.lastcalc.parsers.Parser;
import org.jscience.mathematics.number.*;
import org.jscience.mathematics.number.Number;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

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
public class PrimesUnderParser extends Parser {


    private static TokenList template = TokenList.createD(Lists.<Object> newArrayList("primesunder"),Integer.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }


    @Override
    public ParseResult parse(final TokenList tokens, final int templatePos) {

        final String op = (String) tokens.get(templatePos);
        final Integer input = (Integer) tokens.get(templatePos + 1);

        //result.append(new TokenList.SimpleTokenList("42"));
        if(op.equals("primesunder")){


            final BufferedReader br;
            final String [] primesString;
            try{
                br = new BufferedReader(new InputStreamReader(
                        Bootstrap.class.getResourceAsStream("prime_numbers.txt")));//src/main/resources/com/lastcalc/bootstrap/prime_numbers.txt

                primesString = br.readLine().split(", ");
            }
            catch(Exception e){
                System.err.println("prime_numbers.txt input error");
                return ParseResult.fail();
            }
            
            ArrayList<Integer> allprimes=new ArrayList<Integer>();

            for(int i=0;i<primesString.length;i++){
                allprimes.add(Integer.parseInt(primesString[i]));
            }
            

            Iterator<Integer> allprimesiterator = allprimes.iterator();

            ArrayList<Integer> resultList=new ArrayList<Integer>();
            
            Integer i=allprimesiterator.next();
            while(allprimesiterator.hasNext() && i<=input){
                resultList.add(i);
                i=allprimesiterator.next();
            }
            
            return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), resultList));
        }
        return ParseResult.fail();
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
