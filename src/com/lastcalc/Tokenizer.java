package com.lastcalc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.*;

import com.google.common.collect.Lists;

import org.jscience.mathematics.number.*;


public class Tokenizer {
	private static Pattern p;

	static {
		p = Pattern
				.compile("\\.\\.\\.|\\?|[0-9]*\\.?[0-9]+|[a-zA-Z0-9]+|[()\\[\\]\\{\\}\\:\\+-/*$Û´£%@#\\^]|[=<>!]+|\"(?:[^\"\\\\]|\\\\.)*\"");
	}

	public static TokenList tokenize(final String orig) {
		final Matcher m = p.matcher(orig);

		final ArrayList<Object> ret = Lists.newArrayList();

		while (m.find()) {
			String found = m.group();
			boolean floatingPoint = false, digits = false;
			StringBuilder pureNum = new StringBuilder(found.length());
			for (int x = 0; x < found.length(); x++) {
				final char charAt = found.charAt(x);
				if (Character.isDigit(charAt)) {
					pureNum.append(charAt);
					digits = true;
				} else if (charAt == '.') {
					pureNum.append(charAt);
					if (floatingPoint) { // A number can't have more than one '.'
						pureNum = null;
						break;
					}
					floatingPoint = true;
				} else {
					pureNum = null;
					break;
				}
			}
			if (pureNum != null && digits) {
				if (floatingPoint) {
					final int dotPos = pureNum.indexOf(".");
					final long intPart = Long.parseLong(pureNum.substring(0, dotPos));
					final String fracPart = pureNum.substring(dotPos + 1, pureNum.length());
					final long num = intPart * (long) Math.pow(10, fracPart.length()) + Long.parseLong(fracPart);
					ret.add(Rational.valueOf(num, (long) Math.pow(10, fracPart.length())));
				} else {
					ret.add(LargeInteger.valueOf(pureNum));
				}
			} else if (found.startsWith("\"") && found.endsWith("\"")) {
				found = found.substring(1, found.length() - 1);
				// Unescape any quotes
				found = found.replace("\\\"", "\"");

				ret.add(new QuotedString(found));
			} else {
				ret.add(found);
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
	}
}
