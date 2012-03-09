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
package com.lastcalc.parsers.web;

import java.util.Map;

import com.google.common.collect.*;

import org.jsoup.nodes.Attribute;

import com.lastcalc.*;
import com.lastcalc.cache.ElementWrapper;
import com.lastcalc.parsers.Parser;


public class GetFromElement extends Parser {

	private static TokenList template = TokenList.createD("get", Lists.newArrayList("text", "attributes", "tag"),
			"from", ElementWrapper.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755928813441453688L;

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final String what = (String) tokens.get(templatePos + 1);
		final ElementWrapper ew = (ElementWrapper) tokens.get(templatePos + 3);
		Object ret;
		if (what.equals("text")) {
			ret = new Tokenizer.QuotedString(ew.el.text());
		} else if (what.equals("attributes")) {
			final Map<Object, Object> attributes = Maps.newLinkedHashMap();
			for (final Attribute x : ew.el.attributes()) {
				// TODO: Verify that these are single values when tokenized
				attributes.put(new Tokenizer.QuotedString(x.getKey()), new Tokenizer.QuotedString(x.getValue()));
			}
			ret = TokenList.createD(attributes);
		} else if (what.equals("tag")) {
			ret = TokenList.createD(ew.el.tagName());
		} else
			return ParseResult.fail();
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), ret));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "GetFromElement".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GetFromElement;
	}

}
