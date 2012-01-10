package com.lastcalc.parsers.currency;

import org.junit.Test;

public class CurrenciesTest {

	@Test
	public void test() {
		Currencies.updateExchangeRates();

		System.out.println(Currencies.currenciesByCode);
	}

}
