/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
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
		return "GetFromListOrMap".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GetFromListOrMap;
	}

}
