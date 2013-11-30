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
public class IsPrimeParser extends Parser {

    private static TokenList template = TokenList.createD(Lists.<Object> newArrayList("isprime"),Number.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }


    @Override
    public ParseResult parse(final TokenList tokens, final int templatePos) {

        final String op = (String) tokens.get(templatePos);
        final Integer input = ((LargeInteger)tokens.get(templatePos + 1)).intValue();

        
        if(op.equals("isprime")){
            
            //first, we check if the number is greater than the last element of the primeslist.
            
            //if yes, then we expand the primeslist to be big enough until the last element 
            //      of the primeslist is the largest prime that is still less than the input number.  if the input number is prime
            //      then return true else false
            
            
            //if the input number is less than the last element of the primeslist, then we see if it is in the primeslist
            
            //if it's there then it's prime, if not the false.
            
            
            //run the sqrt n primality test algorithm
            
            final Integer inputSqrRoot=(int)Math.sqrt((double)input);
            
            if(input ==1){
                return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), false));
            }
            else if(input==2 || input==3){
                return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), true));
            }
            
            for(int i=2;i<=inputSqrRoot;i++){
                if(input%i==0){
                    return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), false));
                }
            }

            return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), true));
            
        }
        else{
            return ParseResult.fail();
        }
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
