package com.lastcalc.parsers.collections;

import java.util.List;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

public class ApplyToList extends Parser {
	private static final long serialVersionUID = 9178891850811247754L;
	private static TokenList template = TokenList.createD("apply", UserDefinedParser.class, "to", List.class);

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "ApplyToList".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ApplyToList;
	}

}
