package us.locut;

import java.util.*;
import java.util.regex.*;

import org.jscience.mathematics.number.*;

import us.locut.parsers.*;
import us.locut.parsers.amounts.*;
import us.locut.parsers.datastructures.lists.ListParser;

import com.google.appengine.repackaged.com.google.common.base.Joiner;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class Parsers {
	private static Pattern p;

	public static void getAll(final Collection<Parser> parsers) {
		parsers.addAll(UnitParser.getParsers());
		parsers.add(new TrailingEqualsStripper());
		parsers.add(new AmountParser());
		parsers.add(new DimensionlessAmountParser());
		parsers.addAll(AmountMathOp.getOps());
		parsers.add(new ListParser());
	}

	static {
		p = Pattern.compile("[0-9.]+|[a-zA-Z0-9]+|[\\+-/*=()\\[\\]]|\"(?:[^\"\\\\]|\\\\.)*\"");
	}

	public static Joiner tokenJoiner = Joiner.on(' ');

	public static String toHtml(final ArrayList<Object> tokens) {
		return tokenJoiner.join(tokens);
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

	public static ParsedQuestion parseQuestion(final String question, final Map<String, ArrayList<Object>> variables) {

		List<Object> origTokens = tokenize(question);

		final ParsedQuestion pq = new ParsedQuestion();

		if (origTokens.size() > 2
				&& (origTokens.get(1).equals("=") || origTokens.get(1).toString().equalsIgnoreCase("is"))) {
			pq.variableAssignment = origTokens.get(0).toString();
			origTokens = origTokens.subList(2, origTokens.size());
		}

		pq.question = Lists.newArrayListWithCapacity(origTokens.size() * 2);
		for (final Object token : origTokens) {
			if (token instanceof String) {
				final ArrayList<Object> repl = variables.get(token);
				if (repl != null) {
					pq.question.addAll(repl);
					continue;
				}
			}
			pq.question.add(token);
		}
		return pq;
	}

	public static class ParsedQuestion {
		public String variableAssignment;

		public ArrayList<Object> question;
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
