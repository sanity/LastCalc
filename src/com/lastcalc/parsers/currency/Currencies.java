package com.lastcalc.parsers.currency;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.common.collect.*;

import org.jscience.economics.money.Currency;

import com.lastcalc.*;
import com.lastcalc.parsers.Parser;
import com.lastcalc.parsers.amounts.UnitParser;

public class Currencies {
	private static final Logger log = Logger.getLogger(Currencies.class.getName());

	public static long lastExchangeRateUpdateAttempt = 0;

	public static volatile Map<String, Currency> currenciesByCode;

	public static boolean shouldUpdate() {
		return System.currentTimeMillis() - lastExchangeRateUpdateAttempt > 60l * 60l * 1000l;
	}

	public static List<Parser> getParsers() {
		if (currenciesByCode == null) {
			updateExchangeRates();
		}
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		if (currenciesByCode != null) {
			for (final Entry<String, Currency> e : currenciesByCode.entrySet()) {
				parsers.add(new UnitParser(e.getValue(), TokenList.createD(e.getKey())));
				parsers.add(new UnitParser(e.getValue(), TokenList.createD(e.getKey().toLowerCase())));
			}
			parsers.add(new UnitParser(currenciesByCode.get("USD"), TokenList.createD("$")));
			parsers.add(new UnitParser(currenciesByCode.get("EUR"), TokenList.createD("Û")));
			parsers.add(new UnitParser(currenciesByCode.get("JPY"), TokenList.createD("´")));
			parsers.add(new UnitParser(currenciesByCode.get("GBP"), TokenList.createD("£")));

			parsers.add(new CurrencyReverser());
		}

		return parsers;
	}

	public static void updateExchangeRates() {
		Currency.setReferenceCurrency(Currency.USD);
		try {
			final URL url = new URL("https://raw.github.com/currencybot/open-exchange-rates/master/latest.json");
			final Rates rates = Misc.gson.fromJson(new InputStreamReader(url.openStream()), Rates.class);
			if (!rates.base.equalsIgnoreCase("usd")) {
				log.warning("We assume base rate of USD, but it is " + rates.base);
			}
			final Map<String, Currency> currencies = Maps.newHashMap();
			for (final Entry<String, Double> e : rates.rates.entrySet()) {
				final Currency currency = new Currency(e.getKey());
				currency.setExchangeRate(1.0 / e.getValue());
				currencies.put(e.getKey(), currency);
			}

			currenciesByCode = currencies;
		} catch (final Exception e) {
			log.warning("Couldn't update currency conversion rates " + e);
			e.printStackTrace();
		}
		lastExchangeRateUpdateAttempt = System.currentTimeMillis();
	}

	public static class Rates {
		public String base;
		public Map<String, Double> rates;
	}
}
