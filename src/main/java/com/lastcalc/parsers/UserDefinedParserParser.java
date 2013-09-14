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

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.*;

import org.jscience.mathematics.number.Number;
import org.jscience.physics.amount.Amount;

import com.lastcalc.TokenList;
import com.lastcalc.parsers.PreParser.ListWithTail;
import com.lastcalc.parsers.PreParser.MapWithTail;


public class UserDefinedParserParser extends Parser {

	private static final long serialVersionUID = -6964937711038633291L;
	private static TokenList template;

	static {
		template = TokenList.createD((Lists.<Object> newArrayList("=", "is", "means")));
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {

		// Identify the beginning and end of the UDPP
		final int start = PreParser.findEdgeOrObjectBackwards(tokens, templatePos, null);
		final int end = PreParser.findEdgeOrObjectForwards(tokens, templatePos, null) + 1;

		final TokenList before = tokens.subList(start, templatePos);

		if (before.isEmpty())
			return ParseResult.fail();

		final TokenList after = PreParser.flatten(tokens.subList(templatePos + 1, end));

		if (after.isEmpty())
			return ParseResult.fail();

		final Set<String> variables = Sets.newHashSet();

		for (final Object o : PreParser.flatten(before)) {
			if (o instanceof String && !PreParser.reserved.contains(o)
					&& Character.isUpperCase(((String) o).charAt(0))) {
				variables.add((String) o);
			}
		}

		final UserDefinedParser udp = new UserDefinedParser(before, after, variables);

		return ParseResult.success(
				tokens.replaceWithTokens(Math.max(0, start - 1), Math.min(tokens.size(), end + 1), udp));
	}

	public static class BindException extends Exception {
		private static final long serialVersionUID = -2021162162489202197L;

		public BindException(final String err) {
			super(err);
		}
	}

	@SuppressWarnings("unchecked")
	protected static void bind(final Object from, final Object to, final Set<String> variables,
			final Map<String, Object> bound)
					throws BindException {
		if (from.equals(to) || from.equals("?"))
			return;
		if (from instanceof String && variables.contains(from)) {
            if (bound.containsKey(from) && !bound.get(from).equals(to)) {
                throw new BindException("Variable "+from+" is already bound to "+bound.get(from)+" which isn't the same as "+to);
            }
			bound.put((String) from, to);
		} else if (from instanceof Iterable && to instanceof Iterable) {
			final Iterator<Object> fromL = ((Iterable<Object>) from).iterator();
			final Iterator<Object> toL = ((Iterable<Object>) to).iterator();
			while (fromL.hasNext() && toL.hasNext()) {
				bind(fromL.next(), toL.next(), variables, bound);
			}
			if (fromL.hasNext() || toL.hasNext())
				throw new BindException("Iterables are not of the same size");
		} else if (from instanceof MapWithTail && to instanceof Map) {
			final MapWithTail fromMWT = (MapWithTail) from;
			final Map<Object, Object> toM = (Map<Object, Object>) to;
			final Map<Object, Object> tail = Maps.newLinkedHashMap(toM);
			if (toM.size() < fromMWT.map.size())
				throw new BindException("Map must be at least size of head of MWT");
			for (final Entry<Object, Object> e : fromMWT.map.entrySet()) {
				if (!variables.contains(e.getValue()))
					throw new BindException("Map value '"+e.getValue()+" is not a variable");
				if (variables.contains(e.getKey()) && !bound.containsKey(e.getKey())) {
					// The key is a variable, bind to any value and remove from
					// tail
					final Map.Entry<Object, Object> nextEntry = tail.entrySet().iterator().next();
					bound.put((String) e.getKey(), nextEntry.getKey());
					bind(e.getValue(), nextEntry.getValue(), variables, bound);
					tail.remove(nextEntry.getKey());
				} else {
					final Object aKey = bound.containsKey(e.getKey()) ? bound.get(e.getKey()) : e.getKey();
					final Object value = toM.get(aKey);
					if (value == null)
						throw new BindException("Map doesn't contain key '" + e.getKey() + "'");
					bind(e.getValue(), value, variables, bound);
					tail.remove(aKey);
				}
			}
			bind(fromMWT.tail, tail, variables, bound);
		} else if (from instanceof Map && to instanceof Map) {
			final Map<Object, Object> fromM = (Map<Object, Object>) from;
			final Map<Object, Object> toM = (Map<Object, Object>) to;
			final Map<Object, Object> tail = Maps.newLinkedHashMap(toM);
			if (toM.size() != fromM.size())
				throw new BindException("Maps are not the same size");
			for (final Entry<Object, Object> e : fromM.entrySet()) {
				if (!variables.contains(e.getValue()))
					throw new BindException("Map value '" + e.getValue() + " is not a variable");
				if (variables.contains(e.getKey()) && !bound.containsKey(e.getKey())) {
					// The key is a variable, bind to any value and remove from
					// tail
					final Map.Entry<Object, Object> nextEntry = tail.entrySet().iterator().next();
					bound.put((String) e.getKey(), nextEntry.getKey());
					bind(e.getValue(), nextEntry.getValue(), variables, bound);
					tail.remove(nextEntry.getKey());
				} else {
					final Object aKey = bound.containsKey(e.getKey()) ? bound.get(e.getKey()) : e.getKey();
					final Object value = toM.get(aKey);
					if (value == null)
						throw new BindException("Map doesn't contain key '" + e.getKey() + "'");
					bind(e.getValue(), value, variables, bound);
					tail.remove(e.getKey());
				}
			}
		} else if (from instanceof ListWithTail && to instanceof List) {
			final ListWithTail fromLWT = (ListWithTail) from;
			final List<Object> toList = (List<Object>) to;
			if (toList.size() < fromLWT.list.size())
				throw new BindException("List must be at least size of head of LWT");
			if (toList.isEmpty())
				throw new BindException("Can't bind list with tail to empty list");
			final LinkedList<Object> tail = Lists.newLinkedList(toList);
			for (int x = 0; x < fromLWT.list.size(); x++) {
				bind(fromLWT.list.get(x), toList.get(x), variables, bound);
				tail.removeFirst();
			}
			bind(fromLWT.tail, tail, variables, bound);
		} else
			throw new BindException("Don't know how to bind " + from + ":" + from.getClass().getSimpleName() + " to "
					+ to + ":" + to.getClass().getSimpleName());
	}

	public static class UserDefinedParser extends Parser {
		private static final long serialVersionUID = -4928516936219533258L;

		public final TokenList after;

		private final TokenList template;

		private final TokenList before;

		public final Set<String> variables;

		public UserDefinedParser(final TokenList before, final TokenList after, final Set<String> variables) {
			this.before = before;
			this.after = after;
			this.variables = variables;
			final List<Object> tpl = Lists.newArrayList();

			for (final Object o : before) {
				if (variables.contains(o)) {
					final String var = (String) o;
					if (var.endsWith("List")) {
						tpl.add(List.class);
					} else if (var.endsWith("Map")) {
						tpl.add(Map.class);
					} else if (var.endsWith("Num") || var.endsWith("Number")) {
						tpl.add(Number.class);
					} else if (var.endsWith("Bool") || var.endsWith("Boolean")) {
						tpl.add(Boolean.class);
					} else if (var.endsWith("Amount")) {
						tpl.add(Amount.class);
					} else if (var.endsWith("Fun") || var.endsWith("Function")) {
						tpl.add(UserDefinedParser.class);
						tpl.add(Amount.class);
					} else {
						tpl.add(Object.class);
					}
				} else if (o instanceof String) {
					tpl.add(o);
				} else if (o instanceof MapWithTail || o instanceof Map) {
					tpl.add(Map.class);
				} else if (o instanceof ListWithTail || o instanceof List) {
					tpl.add(List.class);
				} else {
					tpl.add(o.getClass());
				}
			}
			template = TokenList.create(tpl);

		}

		@Override
		public ParseResult parse(final TokenList tokens, final int templatePos, final ParserContext context) {
			if (tokens.get(PreParser.findEdgeOrObjectBackwards(tokens, templatePos, "then")).equals("then"))
				return ParseResult.fail();

			final List<Object> result = Lists.newArrayListWithCapacity(after.size() + 2);
			final boolean useBrackets = tokens.size() > template.size();
			if (useBrackets) {
				result.add("(");
			}
			final TokenList input = tokens.subList(templatePos, templatePos + template.size());
			final Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(variables.size());
			try {
				bind(before, input, variables, varMap);
				for (final Object o : after) {
					final Object val = varMap.get(o);
					if (val != null) {
						result.add(val);
					} else {
						result.add(o);
					}
				}
			} catch (final BindException e) {
				return ParseResult.fail();
			}
			if (useBrackets) {
				result.add(")");
			}
			final TokenList resultTL = TokenList.create(result);
			final TokenList flattened = PreParser.flatten(resultTL);
			return ParseResult.success(
					tokens.replaceWithTokenList(templatePos, templatePos + template.size(), flattened),
					Math.min(0, -flattened.size())
					);
		}

		@Override
		public TokenList getTemplate() {
			return template;
		}

		@Override
		public String toString() {
			return before + " = " + after;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((after == null) ? 0 : after.hashCode());
			result = prime * result + ((before == null) ? 0 : before.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof UserDefinedParser))
				return false;
			final UserDefinedParser other = (UserDefinedParser) obj;
			if (after == null) {
				if (other.after != null)
					return false;
			} else if (!after.equals(other.after))
				return false;
			if (before == null) {
				if (other.before != null)
					return false;
			} else if (!before.equals(other.before))
				return false;
			return true;
		}

		public boolean hasVariables() {
			return !variables.isEmpty();
		}
	}

	@Override
	public TokenList getTemplate() {
		return template;
	}

	@Override
	public int hashCode() {
		return "UserDefinedParserParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof UserDefinedParserParser;
	}

}
