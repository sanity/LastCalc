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
package com.lastcalc;

import java.io.Serializable;
import java.util.*;

import com.google.common.collect.*;

public abstract class TokenList implements Iterable<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6411043222094812410L;

	public static SimpleTokenList create(final List<Object> list) {
		return new SimpleTokenList(Lists.newArrayList(list));
	}

	public static SimpleTokenList createD(final Object... list) {
		return new SimpleTokenList(list);
	}

	public int indexOf(final Object o) {
		for (int x = 0; x < size(); x++) {
			if (get(x).equals(o))
				return x;
		}
		return -1;
	}

	public final void addAllTo(final Collection<Object> coll) {
		for (final Object o : this) {
			coll.add(o);
		}
	}

	@Override
	public String toString() {
		return Misc.joiner.join(this);
	}

	public SubTokenList subList(final int start, final int end) {
		return new SubTokenList(this, start, end);
	}

	public CompositeTokenList replaceWithTokens(final int start, final int end, final Object... replaceWith) {
		return new CompositeTokenList(subList(0, start), new SimpleTokenList(replaceWith), subList(end, size()));
	}

	public CompositeTokenList replaceWithTokenList(final int start, final int end, final TokenList replaceWith) {
		return new CompositeTokenList(subList(0, start), replaceWith, subList(end, size()));
	}

	public CompositeTokenList append(final TokenList... toAppend) {
		final ArrayList<TokenList> tls = Lists.newArrayListWithCapacity(toAppend.length + 1);
		tls.add(this);
		for (int x = 0; x < toAppend.length; x++) {
			tls.add(toAppend[x]);
		}
		return new CompositeTokenList(tls);
	}

	public abstract TLPos getPosInParent(int ix);

	public abstract Object get(int ix);

	public abstract int size();

	public final boolean isEmpty() {
		return size() == 0;
	}

	private int cachedHashCode = 0;

	@Override
	public int hashCode() {
		if (cachedHashCode == 0) {
			int hashCode = 1;
			for (final Object o : this) {
				hashCode = 31 * hashCode + o.hashCode();
			}
			cachedHashCode = hashCode;
		}
		return cachedHashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TokenList))
			return false;
		final TokenList other = (TokenList) obj;
		if (size() != other.size())
			return false;
		for (int x = 0; x < size(); x++) {
			if (!get(x).equals(other.get(x)))
				return false;
		}
		return true;
	}

	@Override
	public Iterator<Object> iterator() {
		return new AbstractIterator<Object>() {

			int pos = 0;

			@Override
			protected Object computeNext() {
				if (pos == TokenList.this.size())
					return endOfData();
				else
					return TokenList.this.get(pos++);
			}
		};
	}

	public static final class SimpleTokenList extends TokenList {
		private static final long serialVersionUID = -6387012802414390386L;
		private final ArrayList<Object> tokens;

		public SimpleTokenList(final Object... tokens) {
			this(Lists.newArrayList(tokens));
		}

		public SimpleTokenList(final ArrayList<Object> tokens) {
			this.tokens = tokens;
		}

		@Override
		public Object get(final int ix) {
			return tokens.get(ix);
		}


		@Override
		public int size() {
			return tokens.size();
		}

		@Override
		public Iterator<Object> iterator() {
			return tokens.iterator();
		}

		@Override
		public TLPos getPosInParent(final int ix) {
			return new TLPos(this, ix);
		}
	}

	public static final class SubTokenList extends TokenList {
		private static final long serialVersionUID = -4306902697632956336L;
		public final TokenList parent;
		private final int start, end;

		public SubTokenList(final TokenList parent, final int start, final int end) {
			this.parent = parent;
			if (end < start)
				throw new IllegalArgumentException("End must be > Start");
			if (start < 0)
				throw new IllegalArgumentException("Start must be >= 0");
			if (end > parent.size())
				throw new IllegalArgumentException("End must be less than or equal to size of parent TokenList");
			this.start = start;
			this.end = end;
		}

		@Override
		public Object get(final int ix) {
			if (start + ix < end)
				return parent.get(ix + start);
			else
				throw new ArrayIndexOutOfBoundsException();
		}

		@Override
		public int size() {
			return end - start;
		}

		@Override
		public TLPos getPosInParent(final int ix) {
			return parent.getPosInParent(start + ix);
		}
	}

	public static final class CompositeTokenList extends TokenList {
		
		private static final long serialVersionUID = 96315179953921395L;
		public final ArrayList<TokenList> tokenLists;
		private final int[] sizes;

		private final Object[] getCache;

		public CompositeTokenList(final TokenList... tokenLists) {
			this(Lists.newArrayList(tokenLists));
		}

		public CompositeTokenList(final ArrayList<TokenList> tokenLists) {
			this.tokenLists = tokenLists;
			sizes = new int[tokenLists.size()];
			for (int x = 0; x < tokenLists.size(); x++) {
				sizes[x] = tokenLists.get(x).size();
			}
			int sz = 0;
			for (int x = 0; x < sizes.length; x++) {
				sz += sizes[x];
			}
			getCache = new Object[sz];
		}

		@Override
		public Object get(final int oix) {
			if (getCache[oix] == null) {
				int l = 0, ix = oix;
				while (ix >= sizes[l]) {
					ix -= sizes[l];
					l++;
					if (l == sizes.length)
						throw new ArrayIndexOutOfBoundsException();
				}
				getCache[oix] = tokenLists.get(l).get(ix);
			}
			return getCache[oix];
		}

		@Override
		public Iterator<Object> iterator() {
			return Iterables.concat(tokenLists).iterator();
		}

		@Override
		public int size() {
			return getCache.length;
		}

		@Override
		public TLPos getPosInParent(int ix) {
			int l = 0;
			while (ix >= sizes[l]) {
				ix -= sizes[l];
				l++;
				if (l == sizes.length)
					throw new ArrayIndexOutOfBoundsException();
			}
			return tokenLists.get(l).getPosInParent(ix);
		}
	}

	public static final class TLPos {
		public final TokenList tl;
		public final int pos;

		public TLPos(final TokenList tl, final int pos) {
			this.tl = tl;
			this.pos = pos;

		}
	}
}
