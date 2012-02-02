package com.lastcalc.parsers.collections;

import java.util.List;

import com.google.common.collect.Lists;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

public class Filter extends Parser {
	private static final long serialVersionUID = 9178891850811247754L;
	private static TokenList template = TokenList.createD("filter", Lists.newArrayList(List.class), "with",
			UserDefinedParser.class);

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		final UserDefinedParser udp = (UserDefinedParser) tokens.get(templatePos + 3);
		final Object datastructure = tokens.get(templatePos + 1);
		List<Object> ret;
		if (datastructure instanceof List) {
			if (udp.variables.size() != 1 || udp.getTemplate().size() != 1)
				return ParseResult.fail();
			final List<Object> list = (List<Object>) datastructure;
			ret = Lists.newArrayListWithCapacity(list.size() * 4);
			ret.add("[");
			for (final Object o : list) {
				final TokenList result = context.parseEngine.parseAndGetLastStep(
						udp.parse(TokenList.createD(o), 0).output, context);
				if (result.size() != 1 || !(result.get(0) instanceof Boolean))
					return ParseResult.fail();
				if (result.get(0).equals(true)) {
					ret.add(o);
					ret.add(",");
				}
			}
			ret.set(ret.size() - 1, "]"); // Overwrite last comma
		} else
			return ParseResult.fail();
		return ParseResult.success(tokens.replaceWithTokenList(templatePos, templatePos + template.size(),
				TokenList.create(ret)));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "Filter".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Filter;
	}

}
