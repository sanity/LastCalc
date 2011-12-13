package us.locut.engines;

import java.util.*;

import us.locut.engines.ParserPickerFactory.ParserPicker;
import us.locut.parsers.ParserContext;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class SimpleParseEngine extends ParseEngine {

	private final ParserPickerFactory ppf;

	public SimpleParseEngine(final ParserPickerFactory ppf) {
		this.ppf = ppf;

	}

	/* (non-Javadoc)
	 * @see us.locut.engines.ParseEngine#parse(java.util.ArrayList, long)
	 */
	@Override
	public LinkedList<ParseStep> parse(final ArrayList<Object> input,
			final ParserContext context) {
		final long startTime = System.currentTimeMillis();
		final ParserPicker picker = ppf.getPicker();
		final LinkedList<ParseStep> steps = Lists.newLinkedList();
		ArrayList<Object> current = input;
		while (System.currentTimeMillis() < context.terminateTime) {
			final ParseStep nextStep = picker.pickNext(current, context);
			if (nextStep == null) {
				break;
			}
			steps.add(nextStep);
			if (nextStep.result.isError()) {
				break;
			}
			current = nextStep.result.output;
		}
		ppf.teach(steps);
		return steps;
	}
}
