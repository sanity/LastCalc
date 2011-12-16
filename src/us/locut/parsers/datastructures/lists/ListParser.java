package us.locut.parsers.datastructures.lists;

import java.util.*;

import us.locut.parsers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class ListParser extends Parser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6139442762692766674L;

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		final LinkedList<Object> list = Lists.newLinkedList();
		int pos = templatePos;
		while (true) {
			pos--;
			list.addFirst(tokens.get(pos));
			pos--;
			if (tokens.get(pos).equals(",")) {
				continue;
			} else if (tokens.get(pos).equals("[")) {
				final ArrayList<Object> ret = Lists.newArrayListWithCapacity(tokens.size() - (templatePos - pos) + 1);
				for (int x = 0; x < pos; x++) {
					ret.add(tokens.get(x));
				}
				ret.add(list);
				for (int x = templatePos + 1; x < tokens.size(); x++) {
					ret.add(tokens.get(x));
				}
				return ParseResult.success(ret);
			} else
				return ParseResult.fail();
		}
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return Lists.<Object> newArrayList("]");
	}

	@Override
	public int hashCode() {
		return "ListParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ListParser;
	}

}
