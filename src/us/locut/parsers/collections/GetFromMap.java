package us.locut.parsers.collections;

import java.util.*;

import com.google.common.collect.Lists;

import us.locut.parsers.Parser;

public class GetFromMap extends Parser {

	private static ArrayList<Object> template = Lists.<Object> newArrayList("get", Object.class, "from", Map.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755928813441453688L;

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final Object toGet = tokens.get(templatePos + 1);
		final Map<Object, Object> map = (Map<Object, Object>) tokens.get(templatePos + 3);
		final Object got = map.get(toGet);
		if (got != null)
			return ParseResult.success(createResponse(tokens, templatePos, got));
		else
			return ParseResult.fail();
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "GetFromMap".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GetFromMap;
	}

}
