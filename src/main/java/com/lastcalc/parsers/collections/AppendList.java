package com.lastcalc.parsers.collections;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;
import com.lastcalc.parsers.ParserContext;

import java.util.List;

/**
 * Created by ian on 6/2/15.
 */
public class AppendList extends Parser {
    private static TokenList template = TokenList.createD(List.class, Lists.newArrayList("append", "+"), List.class);

    @Override
    public TokenList getTemplate() {
        return template;
    }

    @Override
    public int hashCode() {
        return "AppendList".hashCode();
    }

    @Override
    public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
        List<Object> list1 = (List<Object>) tokens.get(templatePos);
        List<Object> list2 = (List<Object>) tokens.get(templatePos+2);
        List<Object> newList = Lists.newArrayList();
        newList.addAll(list1);
        newList.addAll(list2);
        return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos+3, newList));
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
