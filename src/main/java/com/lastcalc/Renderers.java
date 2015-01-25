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

import com.google.common.collect.Lists;
import com.lastcalc.cache.DocumentWrapper;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.parsers.amounts.UnitParser;
import com.lastcalc.parsers.math.Radix;
import org.jscience.economics.money.Currency;
import org.jscience.physics.amount.Amount;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.TransformedUnit;
import javax.measure.unit.Unit;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Renderers {
    private static final Logger log = Logger.getLogger(Renderers.class.getName());


    private static Format currencyFormat = new DecimalFormat("###,###.####");

    private static ArrayList<String> variableColors = Lists.newArrayList("red", "green", "blue", "orange", "rosy",
            "pink");

    public static Element toHtml(final String baseUri, final TokenList tokens) {
        return toHtml(baseUri, tokens, Collections.<String, Integer>emptyMap());
    }

    public static Element toHtml(final String baseUri, final TokenList tokens, final Map<String, Integer> variables) {
        final Element ret = new Element(Tag.valueOf("span"), baseUri);
        for (final Object obj : tokens) {
            renderObject(baseUri, variables, ret, obj);
        }
        return ret;
    }

    private static void renderObject(final String baseUri, final Map<String, Integer> variables,
                                     final Element renderTo,
                                     final Object obj) {
        renderTo.append(" ");
        if (obj instanceof Map) {
            final Map<Object, Object> map = (Map<Object, Object>) obj;
            final int mapSize = map.size();
            final Element mapSpan = renderTo.appendElement("span").addClass("map");
            mapSpan.append("{");
            int count = 0;
            for (final Entry<Object, Object> e : map.entrySet()) {
                renderObject(baseUri, variables, mapSpan, e.getKey());
                mapSpan.append(" :");
                renderObject(baseUri, variables, mapSpan, e.getValue());
                if (count < mapSize - 1) {
                    mapSpan.append(", ");
                }
                count++;
            }
            mapSpan.append("}");
            final int textLength = mapSpan.text().length();
            if (textLength > 120) {
                mapSpan.html("{ too big (" + textLength + " chars) }");
            }
        } else if (obj instanceof List) {
            final List<Object> list = (List<Object>) obj;
            final int listSize = list.size();
            final Element listSpan = renderTo.appendElement("span").addClass("map");
            listSpan.append("[");
            int count = 0;
            for (final Object e : list) {
                renderObject(baseUri, variables, listSpan, e);
                if (count < listSize - 1) {
                    listSpan.append(", ");
                }
                count++;
            }
            listSpan.append("]");
            final int textLength = listSpan.text().length();
            if (textLength > 120) {
                listSpan.html("[ too big (" + textLength + " chars) ]");
            }
        } else if (obj instanceof Amount) {
            final Amount<?> amount = (Amount<?>) obj;
            Unit<? extends Quantity> unit = amount.getUnit();
            log.log(Level.INFO, "Amount: " + amount + ", unit type: " + unit.getClass().getCanonicalName());
            final Element amountSpan = renderTo.appendElement("span").addClass("amount");
            final double estimatedValue = amount.getEstimatedValue();

            if (unit instanceof Currency) {
                final Element currencySpan = amountSpan.appendElement("span").addClass("currency");
                final Currency currency = (Currency) unit;
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
            } else if (unit.equals(NonSI.FAHRENHEIT) || unit.equals(SI.CELSIUS) || unit.equals(SI.KELVIN)) {
                // Avoid "33 fahrenheits"
                final Element temperatureSpan = amountSpan.appendElement("span").addClass("temperature");
                temperatureSpan.text(estimatedValue + unit.toString());
            } else {
                final String numStr = Misc.numberFormat.format(estimatedValue);
                amountSpan.appendElement("span").addClass("number").text(numStr);
                amountSpan.appendText(" ");
                if (!unit.equals(Unit.ONE)) {
                    final Element unitSpan = amountSpan.appendElement("span").addClass("recognized");
                    final String verboseName = estimatedValue == 1.0 ? UnitParser.verboseNamesSing.get(unit)
                            : UnitParser.verboseNamesPlur.get(unit);
                    if (verboseName != null) {
                        unitSpan.text(verboseName);
                    } else {
                        unitSpan.text(unit.toString());
                    }
                }
            }
        } else if (obj instanceof org.jscience.mathematics.number.Number) {
            final org.jscience.mathematics.number.Number<?> num = (org.jscience.mathematics.number.Number<?>) obj;
            final String numStr = Misc.numberFormat.format(num.doubleValue());
            renderTo.appendElement("span").addClass("number").text(numStr);
        } else if (obj instanceof Radix) {
            renderTo.appendElement("span").addClass("number").text(obj.toString());
        } else if (obj instanceof UserDefinedParser) {
            renderTo.appendChild(toHtml(baseUri, ((UserDefinedParser) obj).after));
        } else if (variables.containsKey(obj)) {
            final String color = variableColors.get(variables.get(obj) % variableColors.size());
            renderTo.appendElement("span").addClass("highlighted").addClass("variable").addClass(color)
                    .text((String) obj);
        } else if (obj instanceof String && Character.isUpperCase(((String) obj).charAt(0))) {
            renderTo.appendElement("span").addClass("highlighted").addClass("variable").addClass("white")
                    .text((String) obj);
        } else if (obj instanceof DocumentWrapper) {
            renderTo.append("&lt;html&gt;" + ((DocumentWrapper) obj).title() + " ... &lt;/html&gt;");
        } else {
            renderTo.appendChild(new TextNode(obj.toString(), baseUri));
        }
    }
}
