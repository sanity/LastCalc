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
import org.jsoup.nodes.Document;

// Turns out Document has a crappy hashCode() implementation,
// this makes it more efficient
public class DocumentWrapper implements Serializable {
	private static final long serialVersionUID = -4508748891178262750L;

	public Document doc;

	String asString;

	private int hashCode;

	private String title;

	public DocumentWrapper(final Document doc) {
		this.doc = doc;
		asString = doc.toString();
		hashCode = asString.hashCode();
		title = doc.head().select("title").text();

	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public String title() {
		return title;
	}

	@Override
	public String toString() {
		return asString;
	}

	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeInt(hashCode);
		out.writeObject(title);
		out.writeObject(asString);
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		hashCode = in.readInt();
		title = (String) in.readObject();
		asString = (String) in.readObject();
		doc = Jsoup.parse(asString);
	}
}
