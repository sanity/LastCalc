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
package com.lastcalc.cache;

import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

// Turns out Document has a crappy hashCode() implementation,
// this makes it more efficient
public class ElementWrapper implements Serializable {
	public Element el;

	String asString;

	private int hashCode;

	public ElementWrapper(final Element el) {
		this.el = el;
		asString = el.toString();
		hashCode = asString.hashCode();

	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		if (asString.length() < 60)
			return asString;
		else
			return "<" + el.tagName() + " - too big (" + asString.length() + " chars)>";
	}

	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeInt(hashCode);
		out.writeObject(asString);
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		hashCode = in.readInt();
		asString = (String) in.readObject();
		el = Jsoup.parseBodyFragment(asString).body();
	}
}
