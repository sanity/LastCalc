package us.locut.parsers.amounts;

import java.lang.reflect.*;
import java.util.*;

import javax.measure.unit.*;

import com.google.common.base.Joiner;
import com.google.common.collect.*;

import us.locut.Parsers;
import us.locut.parsers.Parser;

public class UnitParser extends Parser {

	private static final long serialVersionUID = 3254703564962161446L;
	ArrayList<Object> template;
	private final Unit<?> unit;

	public UnitParser(final Unit<?> unit, final ArrayList<Object> template) {
		this.unit = unit;
		this.template = template;
	}

	@Override
	public ParseResult parse(final List<Object> tokens, final int templatePos) {
		return ParseResult.success(createResponse(tokens, templatePos, unit));
	}


	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	public static Set<UnitParser> getParsers() {
		final Set<UnitParser> ret = Sets.newHashSet();
		addParsers(ret, SI.class);
		addParsers(ret, NonSI.class);
		verboseNamesPlur.put(NonSI.FOOT, "feet");
		ret.add(new UnitParser(NonSI.FOOT, Lists.<Object> newArrayList("feet")));
		verboseNamesPlur.put(NonSI.INCH, "inches");
		ret.add(new UnitParser(NonSI.INCH, Lists.<Object> newArrayList("inches")));
		return ret;
	}

	public static Map<Unit<?>, String> verboseNamesSing = Maps.newHashMap();

	public static Map<Unit<?>, String> verboseNamesPlur = Maps.newHashMap();

	private static Set<String> dontUse = Sets.newHashSet("in");

	private static void addParsers(final Set<UnitParser> ret, final Class<? extends SystemOfUnits> cls) {
		final Joiner joiner = Joiner.on(' ');
		for (final Field f : cls.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			final Class<?> fieldType = f.getType();
			if (Unit.class.isAssignableFrom(fieldType)) {
				final Object[] longName = f.getName().toLowerCase().split("_");
				// Don't do this if its a single word of less than 3 characters,
				// it's too easily misinterpreted
				if (longName.length == 1 && longName[0].toString().length() < 3) {
					continue;
				}
				try {
					final Unit<?> unit = (Unit<?>) f.get(null);
					if (longName.length > 0) {
						ret.add(new UnitParser(unit, Lists.<Object> newArrayList(longName)));
						verboseNamesSing.put(unit, joiner.join(longName));
						// And pluralize
						if (longName[0].toString().charAt(longName[0].toString().length() - 1) != 's') {
							final ArrayList<Object> pluralLongName = Lists.<Object> newArrayList(longName);
							pluralLongName.set(0, pluralLongName.get(0) + "s");
							ret.add(new UnitParser(unit, Lists.<Object> newArrayList(pluralLongName)));
							verboseNamesPlur.put(unit, joiner.join(pluralLongName));
						}
					}
					final ArrayList<Object> shortName = Parsers.tokenize(unit.toString());
					// Don't use it if it is only one character, or if its in a
					// list of confusing values like "in"
					if ((!shortName.isEmpty())
							&& ((shortName.size() > 1) || ((shortName.get(0).toString().length() > 1) && (!dontUse
									.contains(shortName.get(0)))))) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UnitParser))
			return false;
		final UnitParser other = (UnitParser) obj;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}
}
