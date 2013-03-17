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

import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;

import com.google.common.collect.*;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.Parser.ParseResult;


public class PreParser extends Parser {
	public static PreParser singleton = new PreParser();

	private static final long serialVersionUID = 3705611710430408505L;

	private static TokenList template;

	public static Set<String> open = Sets.newHashSet("(", "{", "[");

	public static Set<String> close = Sets.newHashSet(")", "}", "]");

	public static Set<String> reserved = Sets.newHashSet(",", ":", "...", "ans");

	static {
		reserved.addAll(open);
		reserved.addAll(close);
		final ArrayList<Object> tpl = Lists.newArrayList();
		tpl.add(Lists.<Object> newArrayList(close));
		template = new TokenList.SimpleTokenList(tpl);
	}

	public static TokenList flatten(final TokenList input) {
		boolean willFlatten = false;
		for (final Object o : input) {
			if (o instanceof TokenList) {
				willFlatten = true;
				break;
			}
			if (o instanceof List) {
				willFlatten = true;
				break;
			}
			if (o instanceof Map) {
				willFlatten = true;
				break;
			}
			if (o instanceof ListWithTail || o instanceof MapWithTail) {
				willFlatten = true;
				break;
			}
		}
		if (!willFlatten)
			return input;

		final List<Object> ret = Lists.newArrayListWithCapacity(input.size() * 4);

		flattenTo(input, ret);

		return TokenList.create(ret);
	}

	private static void flattenTo(final Object obj, final List<Object> output) {
		if (obj instanceof TokenList) {
			final TokenList sts = (TokenList) obj;
			if (sts.size() == 1) {
				flattenTo(sts.get(0), output);
			} else {
				for (final Object o : sts) {
					flattenTo(o, output);
				}
			}
		} else if (obj instanceof List) {
			final List<Object> list = (List<Object>) obj;

			output.add("[");
			for (final Object lo : list) {
				flattenTo(lo, output);
				output.add(",");
			}
			if (list.isEmpty()) {
				output.add("]");
			} else {
				// Overwrite last ","
				output.set(output.size() - 1, "]");
			}
		} else if (obj instanceof Map) {
			final Map<Object, Object> map = (Map<Object, Object>) obj;

			output.add("{");
			for (final Map.Entry<Object, Object> e : map.entrySet()) {
				flattenTo(e.getKey(), output);
				output.add(":");
				flattenTo(e.getValue(), output);
				output.add(",");
			}
			if (map.isEmpty()) {
				output.add("}");
			} else {
				output.set(output.size() - 1, "}");
			}
		} else if (obj instanceof ListWithTail) {
			final ListWithTail lwt = (ListWithTail) obj;
			output.add("[");
			for (final Object lo : lwt.list) {
				flattenTo(lo, output);
				output.add(",");
			}
			output.set(output.size() - 1, "...");
			flattenTo(lwt.tail, output);
			output.add("]");
		} else if (obj instanceof MapWithTail) {
			final MapWithTail mwt = (MapWithTail) obj;

			output.add("{");
			for (final Map.Entry<Object, Object> e : mwt.map.entrySet()) {
				flattenTo(e.getKey(), output);
				output.add(":");
				flattenTo(e.getValue(), output);
				output.add(",");
			}
			output.set(output.size() - 1, "...");
			flattenTo(mwt.tail, output);
			output.add("}");
		} else {
			output.add(obj);
		}
	}

	public static int findEdgeOrObjectBackwards(final TokenList orig, final int startPos, final Object obj) {
		int depth = 0;
		for (int x = startPos-1;; x--) {
			if (x == -1)
				return 0;
			final Object tx = orig.get(x);
			if (depth == 0 && tx.equals(obj))
				return x;
			else if (close.contains(tx)) {
				depth++;
			} else if (open.contains(tx)) {
				if (depth == 0)
					return x+1;
				else {
					depth--;
				}
			} else if (reserved.contains(tx) && depth == 0)
				return x+1;
		}
	}

	public static int findEdgeOrObjectForwards(final TokenList orig, final int startPos, final Object obj) {
		int depth = 0;
		for (int x = startPos + 1;; x++) {
			if (x >= orig.size())
				return orig.size() - 1;
			final Object tx = orig.get(x);
			if (depth == 0 && tx.equals(obj))
				return x;
			else if (open.contains(tx)) {
				depth++;
			} else if (close.contains(tx)) {
				if (depth == 0)
					return x - 1;
				else {
					depth--;
				}
			} else if (reserved.contains(tx) && depth == 0)
				return x - 1;
		}
	}

	public static TokenList enclosedByStructure(final TokenList orig, final int pos) {
		int startPos;
		for (startPos = pos; startPos > -1 && !reserved.contains(orig.get(startPos)); startPos--) {

		}
		startPos++;
		int endPos;
		for (endPos = pos; endPos < orig.size() && !reserved.contains(orig.get(endPos)); endPos++) {

		}
		return orig.subList(startPos, endPos);
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
		if (tokens.get(templatePos).equals(")")) {
			if (tokens.get(templatePos - 2).equals("(") && !reserved.contains(tokens.get(templatePos - 1)))
				return ParseResult.success(tokens.replaceWithTokens(templatePos - 2, templatePos + 1,
						tokens.get(templatePos - 1)));
			else
				return ParseResult.fail();
		} else if (tokens.get(templatePos).equals("]")) {
			// Is there a tail
			int pos = templatePos;
			Object tail = null;
			if (tokens.get(templatePos - 1).equals("[")) {
				pos--;
			} else if (tokens.get(templatePos - 2).equals("...")) {
				tail = tokens.get(templatePos - 1);
				pos -= 2;
			}
			final LinkedList<Object> list = Lists.newLinkedList();
			while (true) {
				final Object posToken = tokens.get(pos);
				if (posToken.equals("[")) {
					break;
				}
				if (!posToken.equals(",") && !posToken.equals("...") && !posToken.equals("]"))
					return ParseResult.fail();
				if (reserved.contains(tokens.get(pos - 1)))
					return ParseResult.fail();
				list.addFirst(tokens.get(pos - 1));
				pos -= 2;
			}
			if (tail == null)
				return ParseResult.success(tokens.replaceWithTokens(pos, templatePos + 1, list));
			else if (tail instanceof Collection) {
				list.addAll((Collection<Object>) tail);
				return ParseResult.success(tokens.replaceWithTokens(pos, templatePos + 1, list));
			} else
				return ParseResult
						.success(tokens.replaceWithTokens(pos, templatePos + 1, new ListWithTail(list, tail)));
		} else if (tokens.get(templatePos).equals("}")) {
			// find start of map declaration
			int startBracket = templatePos - 1;
			while (true) {
				final Object tokenPos = tokens.get(startBracket);
				if (tokenPos.equals("{")) {
					break;
				}
				if (startBracket == 0)
					return ParseResult.fail();
				if (tokenPos.equals("{") || tokenPos.equals("[") || tokenPos.equals("]"))
					return ParseResult.fail();

				startBracket--;
			}

			int pos = startBracket;

			final Map<Object, Object> map = Maps.newLinkedHashMap();
			Object tail = null;
			if (startBracket < templatePos - 1) { // Handle empty map ie. {}
				while (pos < templatePos) {
					final Object posToken = tokens.get(pos);
					if (posToken.equals("{") || posToken.equals(",")) {
						final Object key = tokens.get(pos + 1);
						if (key instanceof String && reserved.contains(key))
							return ParseResult.fail();
						if (!tokens.get(pos + 2).equals(":"))
							return ParseResult.fail();
						final Object value = tokens.get(pos + 3);
						if (value instanceof String && reserved.contains(value))
							return ParseResult.fail();
						map.put(key, value);
					} else if (posToken.equals("...") && pos == templatePos - 2) {
						tail = tokens.get(pos + 1);
					} else
						return ParseResult.fail();
					pos += 4;
				}
			}
			if (tail == null)
				return ParseResult.success(tokens.replaceWithTokens(startBracket, templatePos + 1, map));
			else if (tail instanceof Map) {
				map.putAll((Map<Object, Object>) tail);
				return ParseResult.success(tokens.replaceWithTokens(startBracket, templatePos + 1, map));
			} else
				return ParseResult.success(tokens.replaceWithTokens(startBracket, templatePos + 1, new MapWithTail(map,
						tail)));

		}
		else if(tokens.get(templatePos).equals("ans"))
		{
			return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos, new Integer(1)));
		}
		return ParseResult.fail();
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public int hashCode() {
		return "BracketsParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof PreParser;
	}

	public static class ListWithTail implements Serializable {
		private static final long serialVersionUID = -529889868305161556L;

		public final List<Object> list;

		public final Object tail;

		public ListWithTail(final List<Object> list, final Object tail) {
			this.list = list;
			this.tail = tail;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("ListWithTail [list=");
			builder.append(list);
			builder.append(", tail=");
			builder.append(tail);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((list == null) ? 0 : list.hashCode());
			result = prime * result + ((tail == null) ? 0 : tail.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof ListWithTail))
				return false;
			final ListWithTail other = (ListWithTail) obj;
			if (list == null) {
				if (other.list != null)
					return false;
			} else if (!list.equals(other.list))
				return false;
			if (tail == null) {
				if (other.tail != null)
					return false;
			} else if (!tail.equals(other.tail))
				return false;
			return true;
		}

	}

	public static class MapWithTail implements Serializable {
		private static final long serialVersionUID = -3556614307132116724L;

		public final Map<Object, Object> map;

		public final Object tail;

		public MapWithTail(final Map<Object, Object> map, final Object tail) {
			this.map = map;
			this.tail = tail;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("MapWithTail [map=");
			builder.append(map);
			builder.append(", tail=");
			builder.append(tail);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			result = prime * result + ((tail == null) ? 0 : tail.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof MapWithTail))
				return false;
			final MapWithTail other = (MapWithTail) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			if (tail == null) {
				if (other.tail != null)
					return false;
			} else if (!tail.equals(other.tail))
				return false;
			return true;
		}

	}
}
