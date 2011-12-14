package us.locut.parsers;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Parser implements Serializable {
	private static final long serialVersionUID = -6533682381337736230L;

	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos, final ParserContext context) {
		if (context == null)
			throw new IllegalArgumentException("context is null");
		return parse(tokens, templatePos);
	}

	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
		return parse(tokens, templatePos, null);
	}

	public abstract ArrayList<Object> getTemplate();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	protected final ArrayList<Object> createResponse(final ArrayList<Object> input, final int templatePos,
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

	public final int matchTemplate(final ArrayList<Object> input) {
		return matchTemplate(input, 0);
	}

	public final int matchTemplate(final ArrayList<Object> input, final int startPos) {
		final int templateSize = getTemplate().size();
		templateScan: for (int sPos = startPos; sPos < 1 + input.size() - templateSize; sPos++) {
			for (int x = 0; x < templateSize; x++) {
				final Object templ = getTemplate().get(x);
				final Object src = input.get(sPos + x);
				if (templ instanceof Class) {
					final Class<?> templC = (Class<?>) templ;
					if (!templC.isAssignableFrom(src.getClass())) {
						continue templateScan;
					}
				} else {
					if (!templ.equals(src)) {
						continue templateScan;
					}
				}
			}
			return sPos;
		}
		return -1;
	}

	public static final class ParseResult {

		public static ParseResult error(final String explanation) {
			return new ParseResult(null, explanation);
		}

		public static ParseResult fail() {
			return new ParseResult(null, null);
		}

		public static ParseResult success(final ArrayList<Object> output) {
			return new ParseResult(output, null);
		}

		public static ParseResult success(final ArrayList<Object> output, final String explanation) {
			return new ParseResult(output, explanation);
		}

		private ParseResult(final ArrayList<Object> output, final String explanation) {
			this.output = output;
			this.explanation = explanation;
		}

		public final ArrayList<Object> output;
		public final String explanation;

		public boolean isSuccess() {
			return output != null;
		}

		public boolean isError() {
			return explanation != null;
		}
	}
}
