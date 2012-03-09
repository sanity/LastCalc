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
package com.lastcalc.parsers.math;

import java.io.Serializable;


public class Radix implements Serializable {
	private static final long serialVersionUID = 6793205833102927654L;
	public final long integer;
	public final int radix;

	public static Radix parse(final String value) {
		if (value.startsWith("0x"))
			return new Radix(Long.parseLong(value.substring(2), 16), 16);
		else if (value.startsWith("0b"))
			return new Radix(Long.parseLong(value.substring(2), 2), 2);
		else if (value.startsWith("0o"))
			return new Radix(Long.parseLong(value.substring(2), 8), 8);
		else
			return null;
	}

	public Radix(final long integer, final int radix) {
		this.integer = integer;
		this.radix = radix;
	}

	@Override
	public String toString() {
		String prefix;
		if (radix == 2) {
			prefix = "0b";
		} else if (radix == 8) {
			prefix = "0o";
		} else {
			prefix = "0x";
		}
		return prefix + Long.toString(integer, radix);
	}
}
