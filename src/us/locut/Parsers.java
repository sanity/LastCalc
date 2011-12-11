package us.locut;

import java.util.ArrayList;
import java.util.regex.*;

import org.jscience.mathematics.number.*;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class Parsers {
	private static Pattern p;

	static {
		p = Pattern.compile("[0-9,.]+|[a-zA-Z0-9]+|[\\+-/*=()\\[\\]]");
	}

	public static ArrayList<Object> tokenize(final String orig) {
		final Matcher m = p.matcher(orig);

		final ArrayList<Object> ret = Lists.newArrayList();

		while (m.find()) {
			final String found = m.group();
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
				} else if (charAt != ',') {
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
			} else {
				ret.add(found);
			}
		}

		return ret;
	}
}
