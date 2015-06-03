package com.lastcalc.parsers.math;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;

/**
 * Created by ian on 5/31/15.
 */
public class ImplicitMultiply extends Parser {
    private static TokenList template = TokenList.createD(org.jscience.mathematics.number.Number.class, org.jscience.mathematics.number.Number.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }

    @Override
    public int hashCode() {
        return "ImplicitMultiply".hashCode();
    }

    @Override
    public ParseResult parse(TokenList tokens, int templatePos) {
        Number number1 = (Number) tokens.get(templatePos);
        Number number2 = (Number) tokens.get(templatePos + 1);

        TokenList.CompositeTokenList newTokens = tokens.replaceWithTokens(templatePos, templatePos + 2, number1, "*", number2);
        // Add a score bias so that "twenty five" is parsed as "20+5" and not "20*5"
        return ParseResult.success(newTokens, 1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ImplicitMultiply;
    }
}
