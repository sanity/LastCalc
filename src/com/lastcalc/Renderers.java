package com.lastcalc;

import java.text.*;
import java.util.*;

import javax.measure.unit.Unit;

import com.google.common.collect.Lists;

import org.jscience.economics.money.Currency;
import org.jscience.physics.amount.Amount;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;

import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.parsers.amounts.UnitParser;
import com.lastcalc.parsers.math.Radix;


public class Renderers {
	private static Format currencyFormat = new DecimalFormat("###,###.####");

	private static ArrayList<String> variableColors = Lists.newArrayList("red", "green", "blue", "orange", "rosy",
			"pink");

	public static Element toHtml(final String baseUri, final TokenList tokens) {
		return toHtml(baseUri, tokens, Collections.<String, Integer> emptyMap());
	}

	public static Element toHtml(final String baseUri, final TokenList tokens, final Map<String, Integer> variables) {
		final Element ret = new Element(Tag.valueOf("span"), baseUri);
		for (final Object obj : tokens) {
			ret.append(" ");
			if (obj instanceof Amount) {
				final Amount<?> amount = (Amount<?>) obj;
				final Element amountSpan = ret.appendElement("span");
				final double estimatedValue = amount.getEstimatedValue();
				if (amount.getUnit() instanceof Currency) {
					final Element currencySpan = amountSpan.appendElement("span").addClass("currency");
					final Currency currency = (Currency) amount.getUnit();
					if (currency.getCode().equalsIgnoreCase("USD")) {
						currencySpan.html("US$" + currencyFormat.format(estimatedValue));
					} else if (currency.getCode().equalsIgnoreCase("GBP")) {
						currencySpan.html("&pound;" + currencyFormat.format(estimatedValue));
					} else if (currency.getCode().equalsIgnoreCase("EUR")) {
						currencySpan.html("&euro;" + currencyFormat.format(estimatedValue));
					} else if (currency.getCode().equalsIgnoreCase("JPY")) {
						currencySpan.html("&yen;" + currencyFormat.format(estimatedValue));
					} else {
						currencySpan.text(currencyFormat.format(estimatedValue) + currency.getCode());
					}
				} else {
					final String numStr = Misc.numberFormat.format(estimatedValue);
					amountSpan.appendElement("span").addClass("number").text(numStr);
					amountSpan.appendText(" ");
					if (!amount.getUnit().equals(Unit.ONE)) {
						final Element unitSpan = amountSpan.appendElement("span").addClass("recognized");
						final String verboseName = estimatedValue == 1.0 ? UnitParser.verboseNamesSing.get(amount.getUnit())
								: UnitParser.verboseNamesPlur.get(amount.getUnit());
						if (verboseName != null) {
							unitSpan.text(verboseName);
						} else {
							unitSpan.text(amount.getUnit().toString());
						}
					}
				}
			} else if (obj instanceof org.jscience.mathematics.number.Number) {
				final org.jscience.mathematics.number.Number<?> num = (org.jscience.mathematics.number.Number<?>) obj;
				final String numStr = Misc.numberFormat.format(num.doubleValue());
				ret.appendElement("span").addClass("number").text(numStr);
			} else if (obj instanceof Radix) {
				ret.appendElement("span").addClass("number").text(obj.toString());
			} else if (obj instanceof UserDefinedParser) {
				ret.appendChild(toHtml(baseUri, ((UserDefinedParser) obj).after));
			} else if (variables.containsKey(obj)) {
				final String color = variableColors.get(variables.get(obj) % variableColors.size());
				ret.appendElement("span").addClass("highlighted").addClass("variable").addClass(color)
				.text((String) obj);
			} else if (obj instanceof String && Character.isUpperCase(((String) obj).charAt(0))) {
				ret.appendElement("span").addClass("highlighted").addClass("variable").addClass("white")
				.text((String) obj);

			} else {
				ret.appendChild(new TextNode(obj.toString(), baseUri));
			}
		}
		return ret;
	}
}
