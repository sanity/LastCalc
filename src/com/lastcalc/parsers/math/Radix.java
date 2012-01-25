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
