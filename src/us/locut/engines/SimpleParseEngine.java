package us.locut.engines;

import java.util.*;

import us.locut.parsers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.*;

public class SimpleParseEngine implements ParseEngine {
	LinkedList<Parser> parsers = Lists.newLinkedList();

	public SimpleParseEngine(final Iterable<Parser> parsers) {
		Iterables.addAll(this.parsers, parsers);
	}

	/* (non-Javadoc)
	 * @see us.locut.engines.ParseEngine#parse(java.util.ArrayList, long)
	 */
	@Override
	public synchronized ArrayList<Object> parse(final ArrayList<Object> input, final long maxTimeMillis) {
		final long startTime = System.currentTimeMillis();
		ArrayList<Object> intermediate = input;
		outer: while (true) {
			for (final ListIterator<Parser> li = parsers.listIterator(); li.hasNext();) {
				final Parser p = li.next();
				final ArrayList<Object> template = p.getTemplate();
				templateScan: for (int sPos = 0; sPos < 1 + intermediate.size() - template.size(); sPos++) {
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
					intermediate = p.parse(intermediate, sPos).output;

					System.out.print(p + " -> ");
					for (final Object o : intermediate) {
						System.out.print(o.getClass().getSimpleName() + " : " + o + " , ");
					}

					// And move this parser to the top of the stack
					li.remove();
					parsers.addFirst(p);

					if (System.currentTimeMillis() > startTime + maxTimeMillis)
						return intermediate;

					continue outer;
				}
			}
			return intermediate;
		}
	}
}
