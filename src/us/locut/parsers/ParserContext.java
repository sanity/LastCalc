package us.locut.parsers;

import us.locut.engines.ParseEngine;

public class ParserContext {
	public final ParseEngine parseEngine;

	public final long terminateTime;

	public ParserContext(final ParseEngine parseEngine, final long terminateTime) {
		this.parseEngine = parseEngine;
		this.terminateTime = terminateTime;
	}
}
