package us.locut.parsers.bool;

import java.util.*;

import com.google.common.collect.Lists;

import us.locut.parsers.*;

public class IfThenElse extends Parser {
	private static final long serialVersionUID = -7731508022221902181L;

	private static ArrayList<Object> template = Lists.<Object> newArrayList("if", Boolean.class, "then");

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		final boolean condition = (Boolean) tokens.get(templatePos + 1);
		int elsePos = tokens.size();
		for (int x = templatePos + 3; x < tokens.size(); x++) {
			if (tokens.get(x) instanceof String && ((String) tokens.get(x)).equalsIgnoreCase("else")) {
				elsePos = x;
				break;
			}
		}
		List<Object> result;
		if (condition) {
			result = tokens.subList(3, elsePos);
		} else {
			if (elsePos == tokens.size()) {
				result = Lists.<Object> newArrayList(Boolean.FALSE);
			} else {
				result = tokens.subList(elsePos + 1, tokens.size());
			}
		}
		final List<Object> parsedResult = context.parseEngine.parseAndGetLastStep(result, context);
		final List<Object> ars = Lists.newArrayListWithCapacity(10);
		ars.addAll(tokens.subList(0, templatePos));
		ars.addAll(parsedResult);
		return ParseResult.success(ars);
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
