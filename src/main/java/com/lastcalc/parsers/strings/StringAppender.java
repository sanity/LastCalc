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
package com.lastcalc.parsers.strings;

import com.lastcalc.*;
import com.lastcalc.Tokenizer.QuotedString;
import com.lastcalc.parsers.Parser;

public class StringAppender extends Parser {
	private static final long serialVersionUID = -7820633700412274072L;
	private static TokenList template = TokenList.createD(Object.class, "+", Object.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final Object o1 = tokens.get(templatePos);
		final Object o2 = tokens.get(templatePos + 2);
		QuotedString ret;
		if ((o1 instanceof QuotedString) && (o2 instanceof QuotedString)) {
			ret = new QuotedString(((QuotedString) o1).value + ((QuotedString) o2).value);
		} else if (o1 instanceof QuotedString) {
			ret = new QuotedString(((QuotedString) o1).value + o2.toString());
		} else if (o2 instanceof QuotedString) {
			ret = new QuotedString(o1.toString() + ((QuotedString) o2).value);
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
		return "StringAppender".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof StringAppender;
	}

}
