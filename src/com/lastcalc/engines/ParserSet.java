package com.lastcalc.engines;

import java.util.*;

import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;

/**
 * A set for parsers where the UserDefinedParser that shortens the token
 * list most comes first.  For use in ParserPickerFactories.
 * 
 * @author Ian Clarke <ian.clarke@gmail.com>
 *
 */
public class ParserSet extends TreeSet<Parser> {
	private static final long serialVersionUID = -6285593762627218550L;

	public ParserSet() {
		super(new Comparator<Parser>() {

			@Override
			public int compare(final Parser o1, final Parser o2) {
				if (o1 instanceof UserDefinedParser && !(o2 instanceof UserDefinedParser))
					return -1;
				else if (o2 instanceof UserDefinedParser && !(o1 instanceof UserDefinedParser))
					return 1;
				else if (o1 instanceof UserDefinedParser && o2 instanceof UserDefinedParser) {
					final UserDefinedParser udp1 = (UserDefinedParser) o1;
					final UserDefinedParser udp2 = (UserDefinedParser) o2;
					final int sizeRed1 = udp1.after.size() - udp1.getTemplate().size();
					final int sizeRed2 = udp2.after.size() - udp2.getTemplate().size();
					return compareInts(sizeRed1, sizeRed2, compareInts(o1.hashCode(), o2.hashCode(), 0));
				}
				else
					return compareInts(o1.hashCode(), o2.hashCode(), 0);
			}

		});
	}

	private static int compareInts(final int a, final int b, final int otherwise) {
		if (a<b) return -1;
		else if (a>b) return 1;
		else return otherwise;
	}
}
