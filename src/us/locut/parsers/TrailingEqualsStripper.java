package us.locut.parsers;

import java.util.*;

import com.google.common.collect.Lists;

public class TrailingEqualsStripper extends Parser {
	private static final long serialVersionUID = 2340131974673871340L;

	@Override
	public ArrayList<Object> getTemplate() {
		return Lists.<Object> newArrayList("=");
	}

	@Override
	public int hashCode() {
		return "TrailingEqualsStripper".hashCode();
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		if (templatePos == tokens.size()-1)
			return ParseResult.success(createResponse(tokens, templatePos));
		else
			return ParseResult.fail();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TrailingEqualsStripper;
	}

}
