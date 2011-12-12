package us.locut.parsers.amounts;

import java.lang.reflect.*;
import java.util.*;

import javax.measure.unit.*;

import us.locut.Parsers;
import us.locut.parsers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.*;

public class UnitParser extends Parser {

	private static final long serialVersionUID = 3254703564962161446L;
	ArrayList<Object> template;
	private final Unit<?> unit;

	public UnitParser(final Unit<?> unit, final ArrayList<Object> template) {
		this.unit = unit;
		this.template = template;
	}

	@Override
	public ParseResult parse(final ArrayList<Object> tokens, final int templatePos) {
		return ParseResult.success(createResponse(tokens, templatePos, unit), null);
	}


	@Override
	public int hashCode() {
		return "UnitParser".hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AmountParser;
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	public static Set<UnitParser> getParsers() {
		final Set<UnitParser> ret = Sets.newHashSet();
		addParsers(ret, SI.class);
		addParsers(ret, NonSI.class);
		return ret;
	}

	private static void addParsers(final Set<UnitParser> ret, final Class<? extends SystemOfUnits> cls) {
		for (final Field f : cls.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			final Class<?> fieldType = f.getType();
			if (Unit.class.isAssignableFrom(fieldType)) {
				final Object[] longName = f.getName().toLowerCase().split("_");
				try {
					final Unit<?> unit = (Unit<?>) f.get(null);
					if (longName.length > 0) {
						ret.add(new UnitParser(unit, Lists.newArrayList(longName)));
						// And pluralize
						if (longName[0].toString().charAt(longName[0].toString().length() - 1) != 's') {
							final ArrayList<Object> pluralLongName = Lists.newArrayList(longName);
							pluralLongName.set(0, pluralLongName.get(0) + "s");
							ret.add(new UnitParser(unit, Lists.newArrayList(pluralLongName)));
						}
					}
					final ArrayList<Object> shortName = Parsers.tokenize(unit.toString());
					if (!shortName.isEmpty()) {
						ret.add(new UnitParser(unit, shortName));
					}
				} catch (final IllegalArgumentException e) {
					e.printStackTrace();
				} catch (final IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String toString() {
		return "UnitParser[" + unit.toString() + "]";
	}
}
