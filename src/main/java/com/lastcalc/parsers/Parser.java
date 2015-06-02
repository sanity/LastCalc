/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU Affero General Public License for more
 * details.
 ******************************************************************************/
package com.lastcalc.parsers;

import com.google.common.collect.Sets;
import com.lastcalc.TokenList;
import com.lastcalc.parsers.amounts.AmountConverterParser;
import com.lastcalc.parsers.amounts.AmountParser;
import com.lastcalc.parsers.amounts.UnitParser;
import com.lastcalc.parsers.bool.BoolFunctionsParser;
import com.lastcalc.parsers.bool.BoolParser;
import com.lastcalc.parsers.bool.EqualityParser;
import com.lastcalc.parsers.bool.NotParser;
import com.lastcalc.parsers.collections.*;
import com.lastcalc.parsers.currency.Currencies;
import com.lastcalc.parsers.math.*;
import com.lastcalc.parsers.meta.ImportParser;
import com.lastcalc.parsers.strings.StringAppender;
import com.lastcalc.parsers.web.GetFromElement;
import com.lastcalc.parsers.web.HttpRetriever;
import com.lastcalc.parsers.web.Select;

import java.io.Serializable;
import java.util.*;


public abstract class Parser implements Serializable {
	private static final long serialVersionUID = -6533682381337736230L;

	public static Set<String> reservedTokens = Sets.newHashSet("(", ")", "[", "]", ",", "{", "}", "=", "is");

	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		if (context == null)
			throw new IllegalArgumentException("context is null");
		return parse(tokens, templatePos);
	}

	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return parse(tokens, templatePos, null);
	}

	public abstract TokenList getTemplate();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	protected final ArrayList<Object> createResponseWithCollection(final List<Object> input, final int templatePos,
			final Collection<Object> values) {
		final int tSize = getTemplate().size();
		final ArrayList<Object> response = new ArrayList<Object>(input.size() + values.size() - tSize);
		for (final Object x : input.subList(0, templatePos)) {
			response.add(x);
		}
		for (final Object x : values) {
			response.add(x);
		}
		for (final Object x : input.subList(templatePos + tSize, input.size())) {
			response.add(x);
		}
		return response;
	}

	protected final ArrayList<Object> createResponse(final List<Object> input, final int templatePos,
			final Object... values) {
		final int tSize = getTemplate().size();
		final ArrayList<Object> response = new ArrayList<Object>(input.size() + values.length - tSize);
		for (final Object x : input.subList(0, templatePos)) {
			response.add(x);
		}
        Collections.addAll(response, values);
		for (final Object x : input.subList(templatePos + tSize, input.size())) {
			response.add(x);
		}
		return response;
	}

	public final int matchTemplate(final TokenList input) {
		return matchTemplate(input, 0);
	}

	public final int matchTemplate(final TokenList input, final int startPos) {
		final int templateSize = getTemplate().size();
		templateScan: for (int sPos = startPos; sPos < 1 + input.size() - templateSize; sPos++) {
			for (int x = 0; x < templateSize; x++) {
				final Object templ = getTemplate().get(x);
				final Object src = input.get(sPos + x);
				if (!match(templ, src)) {
					continue templateScan;
				}
			}
			return sPos;
		}
		return -1;
	}

	private boolean match(final Object templ, final Object src) {
		if (templ instanceof Class) {
			final Class<?> templC = (Class<?>) templ;
			return !reservedTokens.contains(src) && templC.isAssignableFrom(src.getClass());
		} else if (templ instanceof Iterable<?>) {
			for (final Object t : ((Iterable<?>) templ)) {
				if (match(t, src))
					return true;
			}
		}
		return templ.equals(src);
	}

	public static void getAll(final Collection<Parser> parsers) {
		parsers.addAll(UnitParser.getParsers());
		parsers.addAll(Currencies.getParsers());
		parsers.add(new TrailingEqualsStripper());
		parsers.add(new AmountParser());
		parsers.add(new UDPApplier());
		parsers.add(new AmountConverterParser());
		parsers.add(new GetFromListOrMap());
		parsers.add(new ApplyTo());
		parsers.add(new Filter());
		parsers.add(new FoldLeft());
		parsers.add(new BoolParser());
		parsers.add(new NotParser());
		parsers.add(new BoolFunctionsParser());
		parsers.add(new EqualityParser());
		parsers.add(new FoldLeft());
		parsers.add(new ToLowerCase());
		parsers.add(new MathBiOp());
		parsers.add(new MathOp());
		parsers.add(new BitwiseOp());
		parsers.add(new RadixConverter());
		parsers.add(new HttpRetriever());
		parsers.add(new Select());
		parsers.add(new GetFromElement());
		parsers.add(new Interpret());
		parsers.add(new StringAppender());
		parsers.add(new ImportParser());
		parsers.add(new FactorialParser());
        parsers.add(new PrimesUnderParser());
        parsers.add(new IsPrimeParser());
        parsers.add(new GCDLCMParser());
		parsers.add(new ImplicitMultiply());
		parsers.add(new AppendList());
	}

	public static final class ParseResult {
		
		public final double scoreBias;
		public final TokenList output;
		
		private ParseResult(final TokenList output, final double scoreBias) {
			this.output = output;
			this.scoreBias = scoreBias;
		}
		
		public static ParseResult fail() {
			return new ParseResult(null, 0);
		}

		public static ParseResult success(final TokenList tokens) {
			return new ParseResult(tokens, 0);
		}

		public static ParseResult success(final TokenList tokens, final double scoreBias) {
			return new ParseResult(tokens, scoreBias);
		}

		
		public boolean isSuccess() {
			return output != null;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("ParseResult [scoreBias=");
			builder.append(scoreBias);
			builder.append(", output=");
			builder.append(output);
			builder.append("]");
			return builder.toString();
		}
	}
}
