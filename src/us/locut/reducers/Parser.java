package us.locut.reducers;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Parser implements Serializable {
	private static final long serialVersionUID = -6533682381337736230L;

	public abstract ParseResult reduce(final ArrayList<Object> tokens, int templatePos);

	public abstract ArrayList<Object> getTemplate();

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

	public static class ParseResult {

		public ParseResult(final ArrayList<Object> output, final String explanation) {
			this.output = output;
			this.explanation = explanation;
		}

		public ArrayList<Object> output;
		String explanation;
	}
}
