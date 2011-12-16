package us.locut.parsers;

import java.io.Serializable;
import java.util.*;

public abstract class Parser implements Serializable {
	private static final long serialVersionUID = -6533682381337736230L;

	public ParseResult parse(final List<Object> tokens, final int templatePos, final ParserContext context) {
		if (context == null)
			throw new IllegalArgumentException("context is null");
		return parse(tokens, templatePos);
	}

	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		return parse(tokens, templatePos, null);
	}

	public abstract ArrayList<Object> getTemplate();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	protected final ArrayList<Object> createResponseWithCollection(final List<Object> input, final int templatePos,
			final Collection<Object> values) {
		final int tSize = getTemplate().size();
		final ArrayList<Object> response = new ArrayList<Object>(input.size() + values.size() - tSize);
		for (final Object x : input.subList(0, templatePos)) {
			response.add(x);
		}
		for (final Object x : values) {
			response.add(x);
		}
		for (final Object x : input.subList(templatePos + tSize, input.size())) {
			response.add(x);
		}
		return response;
	}

	protected final ArrayList<Object> createResponse(final List<Object> input, final int templatePos,
			final Object... values) {
		final int tSize = getTemplate().size();
		final ArrayList<Object> response = new ArrayList<Object>(input.size() + values.length - tSize);
		for (final Object x : input.subList(0, templatePos)) {
			response.add(x);
		}
		for (final Object x : values) {
			response.add(x);
		}
		for (final Object x : input.subList(templatePos + tSize, input.size())) {
			response.add(x);
		}
		return response;
	}

	public final int matchTemplate(final List<Object> input) {
		return matchTemplate(input, 0);
	}

	public final int matchTemplate(final List<Object> input, final int startPos) {
		final int templateSize = getTemplate().size();
		templateScan: for (int sPos = startPos; sPos < 1 + input.size() - templateSize; sPos++) {
			for (int x = 0; x < templateSize; x++) {
				final Object templ = getTemplate().get(x);
				final Object src = input.get(sPos + x);
				if (!match(templ, src)) {
					continue templateScan;
				}
			}
			return sPos;
		}
		return -1;
	}

	private final boolean match(final Object templ, final Object src) {
		if (templ instanceof Class) {
			final Class<?> templC = (Class<?>) templ;
			return templC.isAssignableFrom(src.getClass());
		} else if (templ instanceof Iterable<?>) {
			for (final Object t : ((Iterable<?>) templ)) {
				if (match(t, src))
					return true;
			}
		}
		return templ.equals(src);
	}

	public static final class ParseResult {

		public static ParseResult error(final String explanation) {
			return new ParseResult(null, explanation);
		}

		public static ParseResult fail() {
			return new ParseResult(null, null);
		}

		public static ParseResult success(final List<Object> tokens) {
			return new ParseResult(tokens, null);
		}

		public static ParseResult success(final List<Object> tokens, final String explanation) {
			return new ParseResult(tokens, explanation);
		}

		private ParseResult(final List<Object> output, final String explanation) {
			this.output = output;
			this.explanation = explanation;
		}

		public final List<Object> output;
		public final String explanation;

		public boolean isSuccess() {
			return output != null;
		}

		public boolean isError() {
			return explanation != null;
		}
	}
}
