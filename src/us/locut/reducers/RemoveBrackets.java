package us.locut.reducers;

import java.util.ArrayList;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class RemoveBrackets extends Parser {

	private static final ArrayList<Object> template = Lists.<Object> newArrayList("(", Object.class, ")");

	@Override
	public ParseResult reduce(final ArrayList<Object> tokens, final int templatePos) {
		return new ParseResult(createResponse(tokens, templatePos, tokens.get(templatePos + 1)), null);
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

}
