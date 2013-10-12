package com.lastcalc.parsers.math;

import com.google.common.collect.Lists;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;


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

    private static TokenList template = TokenList.createD(Lists.<Object> newArrayList("isprime"),Integer.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }


    @Override
    public ParseResult parse(final TokenList tokens, final int templatePos) {

        final String op = (String) tokens.get(templatePos);
        final Integer input = (Integer) tokens.get(templatePos + 1);

        
        if(op.equals("isprime")){
            
            final Integer inputSqrRoot=(int)Math.sqrt((double)input);
            
            if(input ==1){
                return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), "false"));
            }
            else if(input==2 || input==3){
                return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), "true"));
            }
            
            for(int i=2;i<=inputSqrRoot;i++){
                if(input%i==0){
                    return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), "false"));
                }
            }

            return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), "true"));
            
        }
        return ParseResult.fail();
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
