package us.locut;

import java.util.*;

import us.locut.reducers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.*;

public class SimpleTransformer {
	LinkedList<Parser> parsers = Lists.newLinkedList();

	public SimpleTransformer(final Iterable<Parser> parsers) {
		Iterables.addAll(this.parsers, parsers);
	}

	public synchronized ArrayList<Object> transform(final ArrayList<Object> input, final long maxTimeMillis) {
		ArrayList<Object> intermediate = input;
		out: while (true) {
			for (final ListIterator<Parser> li = parsers.listIterator(); li.hasNext();) {
				final Parser p = li.next();
				final ArrayList<Object> template = p.getTemplate();
				templateScan: for (int sPos = 0; sPos < intermediate.size() - template.size(); sPos++) {
					for (int x = 0; x < template.size(); x++) {
						final Object templ = template.get(x);
						final Object src = intermediate.get(sPos + x);
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
					// We have a match!
					intermediate = p.reduce(intermediate, sPos).output;

					// And move this parser to the top of the stack
					li.remove();
					parsers.addFirst(p);
				}
			}
			return intermediate;
		}
	}
}
