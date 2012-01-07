package us.locut.parsers.bool;

import us.locut.TokenList;
import us.locut.parsers.*;

public class IfThenElse extends Parser {
	private static final long serialVersionUID = -7731508022221902181L;

	private static TokenList template = TokenList.createD("if", Boolean.class, "then");

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		final boolean condition = (Boolean) tokens.get(templatePos + 1);
		int elsePos = tokens.size();
		for (int x = templatePos + 3; x < tokens.size(); x++) {
			if (tokens.get(x) instanceof String && ((String) tokens.get(x)).equalsIgnoreCase("else")) {
				elsePos = x;
				break;
			}
		}
		TokenList result;
		if (condition) {
			result = tokens.subList(3, elsePos);
		} else {
			if (elsePos == tokens.size()) {
				result = TokenList.createD(Boolean.FALSE);
			} else {
				result = tokens.subList(elsePos + 1, tokens.size());
			}
		}
		return ParseResult.success(tokens.replaceWithTokenList(templatePos, templatePos + template.size(), result));
	}

	@Override
	public int hashCode() {
		return "IfThenElse".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
