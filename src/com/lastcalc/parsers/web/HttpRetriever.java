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

import java.io.IOException;
import java.net.*;

import org.jsoup.Jsoup;

import com.lastcalc.*;
import com.lastcalc.Tokenizer.QuotedString;
import com.lastcalc.cache.*;
import com.lastcalc.parsers.*;

public class HttpRetriever extends Parser {
	private static final long serialVersionUID = 429169446256736079L;
	private static TokenList template = TokenList.createD("retrieve", QuotedString.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		URL url;
		try {
			url = new URL(((QuotedString) tokens.get(templatePos + 1)).value);
		} catch (final MalformedURLException e) {
			return ParseResult.fail();
		}

		final Object cached = ObjectCache.getSlow(1000l * 60l * 60l * 24l, url);

		if (cached != null)
			return ParseResult
					.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), cached));

		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (final IOException e) {
			return ParseResult.fail();
		}
		final String contentType = connection.getContentType();
		if (contentType.toLowerCase().contains("text/html")) {
			DocumentWrapper doc;
			try {
				doc = new DocumentWrapper(Jsoup.parse(connection.getInputStream(), "UTF-8", "/"));
			} catch (final IOException e) {
				return ParseResult.fail();
			}
			ObjectCache.put(1000l * 60l * 60l * 24l, doc, url);
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), doc));
		}
		return ParseResult.fail();

	}

	@Override
	public int hashCode() {
		return "HttpRetriever".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof HttpRetriever;
	}

}
