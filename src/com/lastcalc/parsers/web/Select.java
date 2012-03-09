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

import java.util.List;

import com.google.common.collect.Lists;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.lastcalc.*;
import com.lastcalc.Tokenizer.QuotedString;
import com.lastcalc.cache.*;
import com.lastcalc.parsers.*;

public class Select extends Parser {

	private static final TokenList template = TokenList.createD("select", QuotedString.class, "from",
			Lists.<Object> newArrayList(DocumentWrapper.class, ElementWrapper.class));

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		final String selectStatement = ((QuotedString) tokens.get(templatePos + 1)).value;
		final Object docOrElement = tokens.get(templatePos + 3);
		final Object cached = ObjectCache.getFast(Long.MAX_VALUE, "select", selectStatement, docOrElement);
		if (cached != null)
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos+template.size(), cached));
		Element el;
		if (docOrElement instanceof DocumentWrapper) {
			el = ((DocumentWrapper) docOrElement).doc;
		} else {
			el = ((ElementWrapper) docOrElement).el;
		}
		final Elements elements = el.select(selectStatement);

		if (elements.size() == 1)
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos+template.size(), new ElementWrapper(elements.first())));
		else {
			final List<Object> elementList = Lists.newArrayListWithCapacity(elements.size());
			for (final Element e : elements) {
				elementList.add(new ElementWrapper(e));
			}
			return ParseResult
					.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), elementList));
		}
	}

	@Override
	public int hashCode() {
		return "Select".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Select;
	}

}
