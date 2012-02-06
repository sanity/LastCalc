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
