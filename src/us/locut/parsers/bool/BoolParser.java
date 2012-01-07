package us.locut.parsers.bool;

import com.google.common.collect.Lists;

import us.locut.TokenList;
import us.locut.parsers.Parser;

public class BoolParser extends Parser {
	private static final long serialVersionUID = -7666856639055196045L;
	private static TokenList template = TokenList.createD(Lists.newArrayList("true", "yes", "false", "no"));


	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final boolean result = (tokens.get(templatePos).equals("true") || tokens.get(templatePos).equals("yes"));
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), result));
	}

	@Override
	public int hashCode() {
		return "BoolParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BoolParser;
	}

}
