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
package com.lastcalc.parsers;

import com.lastcalc.engines.ParseEngine;

public class ParserContext implements Cloneable {
	public final ParseEngine parseEngine;

	public int importDepth = 0;

	public long timeout;

	public ParserContext(final ParseEngine parseEngine, final long timeout, final int importDepth) {
		this(parseEngine, timeout);
		this.importDepth = importDepth;
	}

	public ParserContext(final ParseEngine parseEngine, final long timeout) {
		this.parseEngine = parseEngine;
		this.timeout = timeout;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
