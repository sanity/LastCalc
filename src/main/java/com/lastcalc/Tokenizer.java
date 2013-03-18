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
package com.lastcalc;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lastcalc.parsers.math.Radix;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Rational;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
	private static Pattern anyMatcher;

	private static Map<String, Pattern> matchers = Maps.newHashMap();

	static {
		final Map<String, String> tokenMatchers = Maps.newLinkedHashMap();
		tokenMatchers.put("threeDots", "\\.\\.\\.");
		tokenMatchers.put("question", "\\?");
		tokenMatchers.put("octalNum", "0o[0-7]+");
		tokenMatchers.put("binaryNum", "0b[10]+");
		tokenMatchers.put("hexNum", "0x[0-9a-f]+");
		// Important that this comes before integer
		tokenMatchers.put("float", "[0-9]+\\.[0-9]+");
		tokenMatchers.put("integer", "[0-9]+");
		tokenMatchers.put("token", "[a-zA-Z][a-zA-Z0-9]*");
		tokenMatchers.put("miscSymbols", "[()\\[\\]\\{\\}\\:\\+-/*$۴�%@#\\^]");
		tokenMatchers.put("equalitySymbols", "[=<>!]+");
		tokenMatchers.put("quotedString", "\"(?:[^\"\\\\]|\\\\.)*\"");
		final String anyMatcher = Joiner.on('|').join(tokenMatchers.values());

		for (final Entry<String, String> e : tokenMatchers.entrySet()) {
			matchers.put(e.getKey(), Pattern.compile("^" + e.getValue() + "$"));
		}

		Tokenizer.anyMatcher = Pattern.compile(anyMatcher);
	}

	public static TokenList tokenize(final String orig) {
		final Matcher m = anyMatcher.matcher(orig);

		final ArrayList<Object> ret = Lists.newArrayList();

		while (m.find()) {
			final String found = m.group();
			if (matchers.get("float").matcher(found).find()) {
				final int dotPos = found.indexOf(".");
				final long intPart = Long.parseLong(found.substring(0, dotPos));
				final String fracPart = found.substring(dotPos + 1, found.length());
				final long num = intPart * (long) Math.pow(10, fracPart.length()) + (intPart >= 0 ? 1 : -1)
						* Long.parseLong(fracPart);
				final long denom = (long) Math.pow(10, fracPart.length());
				ret.add(Rational.valueOf(num, denom));
			} else if (matchers.get("integer").matcher(found).find()){
				ret.add(LargeInteger.valueOf(found));
			} else if (found.startsWith("\"") && found.endsWith("\"")) {
				final String quoted = found.substring(1, found.length() - 1).replace("\\\"", "\"");

				ret.add(new QuotedString(quoted));
			} else {
				// Is it a radix?
				final Radix radix = Radix.parse(found);
				if (radix != null) {
					ret.add(radix);
				} else {
					ret.add(found);
				}
			}
		}

		return TokenList.create(ret);
	}


	public static class QuotedString implements Serializable {
		private static final long serialVersionUID = 8125107374281852751L;
		public final String value;

		public QuotedString(final String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "\"" + value + "\"";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof QuotedString))
				return false;
			final QuotedString other = (QuotedString) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
	}
}
