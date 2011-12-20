package us.locut.parsers;

import us.locut.engines.ParseEngine;

public class ParserContext implements Cloneable {
	public final ParseEngine parseEngine;

	public long timeout;

	public ParserContext(final ParseEngine parseEngine, final long timeout) {
		this.parseEngine = parseEngine;
		this.timeout = timeout;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
