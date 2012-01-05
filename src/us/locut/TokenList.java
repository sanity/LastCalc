package us.locut;

import java.util.*;

import com.google.common.collect.*;

public abstract class TokenList implements Iterable<Object> {

	public int indexOf(final Object o) {
		for (int x = 0; x < size(); x++) {
			if (get(x).equals(o))
				return x;
		}
		return -1;
	}

	public SubTokenList subList(final int start, final int end) {
		return new SubTokenList(this, start, end);
	}

	public CompositeTokenList replace(final int start, final int end, final TokenList replaceWith) {
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

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (final Object o : this) {
			hashCode = 31 * hashCode + o.hashCode();
		}
		return hashCode;
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
		public final TokenList parent;
		private final int start, end;

		public SubTokenList(final TokenList parent, final int start, final int end) {
			this.parent = parent;
			if (end <= start)
				throw new IllegalArgumentException("End must be > Start");
			if (start < 0)
				throw new IllegalArgumentException("Start must be >= 0");
			if (end >= parent.size())
				throw new IllegalArgumentException("End must be less than size of parent TokenList");
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
		public final ArrayList<TokenList> tokenLists;
		private final int[] sizes;

		public CompositeTokenList(final TokenList... tokenLists) {
			this(Lists.newArrayList(tokenLists));
		}

		public CompositeTokenList(final ArrayList<TokenList> tokenLists) {
			this.tokenLists = tokenLists;
			sizes = new int[tokenLists.size()];
			for (int x = 0; x < tokenLists.size(); x++) {
				sizes[x] = tokenLists.get(x).size();
			}
		}

		@Override
		public Object get(int ix) {
			int l = 0;
			while (ix >= sizes[l]) {
				ix -= sizes[l];
				l++;
				if (l == sizes.length)
					throw new ArrayIndexOutOfBoundsException();
			}
			return tokenLists.get(l).get(ix);
		}

		@Override
		public Iterator<Object> iterator() {
			return Iterables.concat(tokenLists).iterator();
		}

		@Override
		public int size() {
			int sz = 0;
			for (int x = 0; x < sizes.length; x++) {
				sz += sizes[x];
			}
			return sz;
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
