package com.lastcalc.parsers.math;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

/**
 * Created by ian on 5/31/15.
 */
public class VarMultiply extends Parser {
    private static TokenList template = TokenList.createD(org.jscience.mathematics.number.Number.class, String.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }

    @Override
    public int hashCode() {
        return "VarMultiply".hashCode();
    }

    @Override
    public ParseResult parse(TokenList tokens, int templatePos) {
        Number number = (Number) tokens.get(templatePos);
        String var = (String) tokens.get(templatePos+1);
        if (var.length() != 1 || !Character.isAlphabetic(var.charAt(0))) {
            return ParseResult.fail();
        } else {
            TokenList.CompositeTokenList newTokens = tokens.replaceWithTokens(templatePos, templatePos + 2, number, "*", var);
            return ParseResult.success(newTokens);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VarMultiply;
    }
}
