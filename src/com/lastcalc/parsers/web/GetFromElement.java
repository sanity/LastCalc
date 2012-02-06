package com.lastcalc.parsers.web;

import java.util.Map;

import com.google.common.collect.*;

import org.jsoup.nodes.Attribute;

import com.lastcalc.*;
import com.lastcalc.cache.ElementWrapper;
import com.lastcalc.parsers.Parser;


public class GetFromElement extends Parser {

	private static TokenList template = TokenList.createD("get", Lists.newArrayList("text", "attributes", "tag"),
			"from", ElementWrapper.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755928813441453688L;

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		final String what = (String) tokens.get(templatePos + 1);
		final ElementWrapper ew = (ElementWrapper) tokens.get(templatePos + 3);
		Object ret;
		if (what.equals("text")) {
			ret = new Tokenizer.QuotedString(ew.el.text());
		} else if (what.equals("attributes")) {
			final Map<Object, Object> attributes = Maps.newLinkedHashMap();
			for (final Attribute x : ew.el.attributes()) {
				// TODO: Verify that these are single values when tokenized
				attributes.put(new Tokenizer.QuotedString(x.getKey()), new Tokenizer.QuotedString(x.getValue()));
			}
			ret = TokenList.createD(attributes);
		} else if (what.equals("tag")) {
			ret = TokenList.createD(ew.el.tagName());
		} else
			return ParseResult.fail();
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), ret));
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "GetFromElement".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GetFromElement;
	}

}
