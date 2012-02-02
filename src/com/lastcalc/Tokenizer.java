package com.lastcalc;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.*;

import com.google.common.base.Joiner;
import com.google.common.collect.*;

import org.jscience.mathematics.number.*;

import com.lastcalc.parsers.math.Radix;

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
		tokenMatchers.put("miscSymbols", "[()\\[\\]\\{\\}\\:\\+-/*$Û´£%@#\\^]");
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
