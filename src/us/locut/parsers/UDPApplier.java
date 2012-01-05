package us.locut.parsers;

import java.util.*;

import com.google.common.collect.Lists;

import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;

public class UDPApplier extends Parser {
	private static final long serialVersionUID = -5412238263419670848L;
	private static ArrayList<Object> template = Lists.<Object> newArrayList("apply", UserDefinedParser.class, "to");

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		final UserDefinedParser function = (UserDefinedParser) tokens.get(templatePos + 1);
		if (2 + templatePos + function.getTemplate().size() > tokens.size())
			return ParseResult.fail();
		final List<Object> input = tokens.subList(templatePos + 3, templatePos + 3 + function.getTemplate().size());
		if (function.matchTemplate(input) != 0)
			return ParseResult.fail();
		final ParseResult parse = function.parse(input, 0, context);
		if (parse.isSuccess()) {
			final List<Object> result = Lists.newArrayListWithCapacity(tokens.size() + function.after.size());
			result.addAll(tokens.subList(0, templatePos));
			result.addAll(parse.output);
			result.addAll(tokens.subList(templatePos + 3 + function.getTemplate().size(), tokens.size()));
			return ParseResult.success(result, parse.scoreBias);

		} else
			return ParseResult.fail();
	}

	@Override
	public int hashCode() {
		return "UDPApplier".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof UDPApplier;
	}

}
