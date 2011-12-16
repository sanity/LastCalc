package us.locut;

import java.util.*;
import java.util.regex.*;

import org.jscience.mathematics.number.*;

import us.locut.parsers.*;
import us.locut.parsers.amounts.*;
import us.locut.parsers.datastructures.lists.ListParser;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class Parsers {
	private static Pattern p;

	public static void getAll(final Collection<Parser> parsers) {
		parsers.addAll(UnitParser.getParsers());
		parsers.add(new TrailingEqualsStripper());
		parsers.add(new AmountParser());
		parsers.add(new AmountConverterParser());
		parsers.add(new DimensionlessAmountParser());
		parsers.add(new ListParser());
	}

	static {
		p = Pattern.compile("[0-9.]+|[a-zA-Z0-9]+|[\\+-/*=()\\[\\]]|\"(?:[^\"\\\\]|\\\\.)*\"");
	}

	public static ArrayList<Object> tokenize(final String orig) {
		final Matcher m = p.matcher(orig);

		final ArrayList<Object> ret = Lists.newArrayList();

		while (m.find()) {
			String found = m.group();
			boolean decimal = false, digits = false;
			StringBuilder pureNum = new StringBuilder(found.length());
			for (int x = 0; x < found.length(); x++) {
				final char charAt = found.charAt(x);
				if (Character.isDigit(charAt)) {
					pureNum.append(charAt);
					digits = true;
				} else if (charAt == '.') {
					pureNum.append(charAt);
					decimal = true;
				} else {
					pureNum = null;
					break;
				}
			}
			if (pureNum != null && digits) {
				if (decimal) {
					ret.add(FloatingPoint.valueOf(pureNum));
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

		return ret;
	}


	public static class QuotedString {
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
