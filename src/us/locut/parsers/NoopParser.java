package us.locut.parsers;

import java.util.ArrayList;

public class NoopParser extends Parser {

	public static final NoopParser singleton = new NoopParser();

	@Override
	public ArrayList<Object> getTemplate() {
		return null;
	}

	@Override
	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
		return ParseResult.success(tokens);
	}

	@Override
	public int hashCode() {
		return "NoopParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof NoopParser;
	}

}
