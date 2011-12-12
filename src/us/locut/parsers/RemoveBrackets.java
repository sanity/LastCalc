package us.locut.parsers;

import java.util.ArrayList;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class RemoveBrackets extends Parser {

	private static final long serialVersionUID = -3236089469608833035L;
	private static final ArrayList<Object> template = Lists.<Object> newArrayList("(", Object.class, ")");

	@Override
	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
		return ParseResult.success(createResponse(tokens, templatePos, tokens.get(templatePos + 1)), null);
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "RemoveBrackets".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof RemoveBrackets;
	}

}
