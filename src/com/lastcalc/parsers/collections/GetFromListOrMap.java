package com.lastcalc.parsers.collections;

import java.util.*;

import com.google.common.collect.Lists;

import org.jscience.mathematics.number.LargeInteger;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser;


public class GetFromListOrMap extends Parser {

	private static TokenList template = TokenList.createD("get", Object.class, "from",
			Lists.newArrayList(Map.class, List.class));

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755928813441453688L;

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Object toGet = tokens.get(templatePos + 1);
		if (tokens.get(templatePos + 3) instanceof Map) {
			final Map<Object, Object> map = (Map<Object, Object>) tokens.get(templatePos + 3);
			final Object got = map.get(toGet);
			if (got != null)
				return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), got));
			else
				return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), false));
		} else if (tokens.get(templatePos + 3) instanceof List) {
			final List<Object> list = (List<Object>) tokens.get(templatePos + 3);
			if (toGet instanceof LargeInteger) {
				final int ix = ((LargeInteger) toGet).intValue();
				if (ix < 0 || ix >= list.size())
					return ParseResult.fail();
				else
					return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(),
							list.get(ix)));
			} else
				return ParseResult.fail();
		} else
			return ParseResult.fail();
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "GetFromMap".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GetFromListOrMap;
	}

}
