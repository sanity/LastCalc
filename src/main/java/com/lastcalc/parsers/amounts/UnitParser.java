/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
package com.lastcalc.parsers.amounts;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.measure.unit.*;

import com.google.common.base.Joiner;
import com.google.common.collect.*;

import com.lastcalc.*;
import com.lastcalc.parsers.Parser;


public class UnitParser extends Parser {
	private static final Logger log = Logger.getLogger(UnitParser.class.getName());


	private static final long serialVersionUID = 3254703564962161446L;
	public final TokenList template;
	public final Unit<?> unit;

	public UnitParser(final Unit<?> unit, final TokenList template) {
		this.unit = unit;
		this.template = template;
	}

	@Override
	public ParseResult parse(final TokenList tokens, final int templatePos) {
		return ParseResult.success(tokens.replaceWithTokens(templatePos, templatePos + template.size(), unit));
	}


	@Override
	public TokenList getTemplate() {
		return template;
	}

	public static Set<UnitParser> getParsers() {
		final Set<UnitParser> ret = Sets.newHashSet();
		addParsers(ret, SI.class);

		// Add various prefixes, iterate through a copy of ret
		// to prevent concurrent modification exception
		
		for (final UnitParser up : Sets.newHashSet(ret)) {
			if (up.template.size() > 1){ continue;}
			ret.add(new UnitParser(SI.ATTO(up.unit), TokenList.createD("atto" + up.template.get(0))));
			ret.add(new UnitParser(SI.CENTI(up.unit), TokenList.createD("centi" + up.template.get(0))));
			ret.add(new UnitParser(SI.DECI(up.unit), TokenList.createD("deci" + up.template.get(0))));
			ret.add(new UnitParser(SI.DEKA(up.unit), TokenList.createD("deka" + up.template.get(0))));
			ret.add(new UnitParser(SI.EXA(up.unit), TokenList.createD("exa" + up.template.get(0))));
			ret.add(new UnitParser(SI.FEMTO(up.unit), TokenList.createD("femto" + up.template.get(0))));
			ret.add(new UnitParser(SI.GIGA(up.unit), TokenList.createD("giga" + up.template.get(0))));
			ret.add(new UnitParser(SI.HECTO(up.unit), TokenList.createD("hecto" + up.template.get(0))));
			ret.add(new UnitParser(SI.KILO(up.unit), TokenList.createD("kilo" + up.template.get(0))));
			ret.add(new UnitParser(SI.MEGA(up.unit), TokenList.createD("mega" + up.template.get(0))));
			ret.add(new UnitParser(SI.MICRO(up.unit), TokenList.createD("micro" + up.template.get(0))));
			ret.add(new UnitParser(SI.MILLI(up.unit), TokenList.createD("milli" + up.template.get(0))));
			ret.add(new UnitParser(SI.NANO(up.unit), TokenList.createD("nano" + up.template.get(0))));
			ret.add(new UnitParser(SI.PETA(up.unit), TokenList.createD("peta" + up.template.get(0))));
			ret.add(new UnitParser(SI.PICO(up.unit), TokenList.createD("pico" + up.template.get(0))));
			ret.add(new UnitParser(SI.TERA(up.unit), TokenList.createD("tera" + up.template.get(0))));
			ret.add(new UnitParser(SI.YOCTO(up.unit), TokenList.createD("yocto" + up.template.get(0))));
			ret.add(new UnitParser(SI.ZEPTO(up.unit), TokenList.createD("zepto" + up.template.get(0))));
			ret.add(new UnitParser(SI.ZETTA(up.unit), TokenList.createD("zetta" + up.template.get(0))));
		}

		addParsers(ret, NonSI.class);
		verboseNamesPlur.put(NonSI.FOOT, "feet");
		ret.add(new UnitParser(NonSI.FOOT, TokenList.createD("feet")));
		verboseNamesPlur.put(NonSI.INCH, "inches");
		ret.add(new UnitParser(NonSI.INCH, TokenList.createD("inches")));
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
						ret.add(new UnitParser(unit, TokenList.create(Lists.newArrayList(longName))));
						verboseNamesSing.put(unit, joiner.join(longName));
						// And pluralize
						if (longName[0].toString().charAt(longName[0].toString().length() - 1) != 's') {
							final ArrayList<Object> pluralLongName = Lists.<Object> newArrayList(longName);
							pluralLongName.set(0, pluralLongName.get(0) + "s");
							ret.add(new UnitParser(unit, TokenList.create(pluralLongName)));
							verboseNamesPlur.put(unit, joiner.join(pluralLongName));
						}
					}
					final TokenList shortName = Tokenizer.tokenize(unit.toString());
					// Don't use it if it is only one character, or if its in a
					// list of confusing values like "in"
					if (shortName.size() > 0) {
						if (((shortName.size() > 1) || ((shortName.get(0).toString().length() > 1) && (!dontUse
								.contains(shortName.get(0)))))) {
							ret.add(new UnitParser(unit, shortName));
						}
					}
				} catch (final IllegalArgumentException e) {
					log.log(Level.WARNING, "Error parsing unit", e);
				} catch (final IllegalAccessException e) {
					log.log(Level.WARNING, "Error parsing unit", e);
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
