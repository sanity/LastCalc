package us.locut.parsers.collections;

import java.util.*;

import com.google.common.collect.Lists;

import us.locut.parsers.*;
import us.locut.parsers.PreParser.SubTokenSequence;
import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;

public class MapToParser extends Parser {
	private static final long serialVersionUID = -4682034993337702705L;
	private static ArrayList<Object> template;

	static {
		template = Lists.newArrayList();
		template.add("map");
		template.add(UserDefinedParser.class);
		template.add("to");
		template.add(Lists.newArrayList(List.class, Map.class));
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		final UserDefinedParser parser = (UserDefinedParser) tokens.get(templatePos + 1);
		if (tokens.get(templatePos + 3) instanceof List) {
			if (parser.variables.size() != 1)
				return ParseResult.fail();
			final List<Object> origList = (List<Object>) tokens.get(templatePos + 3);
			final List<Object> ret = Lists.newArrayListWithCapacity(origList.size());
			for (final Object o : origList) {
				final List<Object> apl = parser.apply(o);
				final List<Object> parsed = context.parseEngine.parseAndGetLastStep(apl, context);
				if (parsed.size() == 1) {
					ret.add(parsed.get(0));
				} else {
					ret.add(new SubTokenSequence(parsed));
				}
			}
			return ParseResult.success(createResponse(tokens, templatePos, ret));
		} else
			throw new RuntimeException();
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "MapToParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof MapToParser;
	}

}
