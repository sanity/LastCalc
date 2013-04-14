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
package com.lastcalc;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.NumberFormat;
import java.util.Random;

public class Misc {
	public static Random rand = new Random();

	private static final String randStrChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz"
			+ "0123456789";

	public static String toString(final Iterable<Object> o) {
		final StringBuilder sb = new StringBuilder();
		for (final Object io : o) {
			sb.append(io.getClass().getSimpleName() + " " + io + ", ");
		}
		return sb.toString();
	}

	public static String randomString(final int digits) {
		final StringBuilder sb = new StringBuilder(digits);
		for (int x = 0; x < digits; x++) {
			sb.append(randStrChars.charAt(rand.nextInt(randStrChars.length())));
		}
		return sb.toString();
	}

	public static final Gson gson = new GsonBuilder().create();

	public static NumberFormat numberFormat;
	static {
		numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
		numberFormat.setParseIntegerOnly(false);
		numberFormat.setMaximumFractionDigits(20);
	}

	public static final Joiner joiner = Joiner.on(' ');
}
