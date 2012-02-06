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
		return "BoolFunctionsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof StringAppender;
	}

}
