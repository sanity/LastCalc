package us.locut;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;

import com.google.common.collect.Lists;

import org.jscience.mathematics.number.*;

import us.locut.parsers.*;
import us.locut.parsers.amounts.*;
import us.locut.parsers.bool.*;
import us.locut.parsers.collections.GetFromMap;

public class Parsers {
	private static Pattern p;

	public static void getAll(final Collection<Parser> parsers) {
		parsers.addAll(UnitParser.getParsers());
		parsers.add(new TrailingEqualsStripper());
		parsers.add(new AmountParser());
		parsers.add(new UDPApplier());
		parsers.add(new AmountConverterParser());
		parsers.add(new DimensionlessAmountParser());
		parsers.add(new GetFromMap());
		parsers.add(new IfThenElse());
		parsers.add(new BoolParser());

	}

	static {
		p = Pattern
				.compile("\\.\\.\\.|\\?|[0-9]*\\.?[0-9]+|[a-zA-Z0-9]+|[\\+-/*=()\\[\\]\\{\\}\\:]|\"(?:[^\"\\\\]|\\\\.)*\"");
	}

	public static TokenList tokenize(final String orig) {
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
					if (decimal) { // A number can't have more than one '.'
						pureNum = null;
						break;
					}
					decimal = true;
				} else {
					pureNum = null;
					break;
				}
			}
			if (pureNum != null && digits) {
				if (decimal) {
					ret.add(Real.valueOf(pureNum));
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
