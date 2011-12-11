package us.locut.reducers.amounts;

import java.util.*;

import javax.measure.unit.*;

import us.locut.Parsers;
import us.locut.reducers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.Sets;

public class UnitParser extends Parser {

	ArrayList<Object> template;
	private final Unit<?> unit;

	public UnitParser(final Unit<?> unit) {
		this.unit = unit;
		template = Parsers.tokenize(unit.toString());
	}

	@Override
	public ParseResult reduce(final ArrayList<Object> tokens, final int templatePos) {
		return new ParseResult(createResponse(tokens, templatePos, unit), null);
	}

	@Override
	public ArrayList<Object> getTemplate() {
		return template;
	}

	public static Set<UnitParser> getParsers() {
		final Set<UnitParser> ret = Sets.newHashSet();
		for (final Unit<?> unit : Sets.union(NonSI.getInstance().getUnits(), SI.getInstance().getUnits())) {
			ret.add(new UnitParser(unit));
		}
		return ret;
	}

}
