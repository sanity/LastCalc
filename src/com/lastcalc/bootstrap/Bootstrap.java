package com.lastcalc.bootstrap;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;

import com.lastcalc.parsers.UserDefinedParserParser;

public class Bootstrap {

	private static final Logger log = Logger.getLogger(Bootstrap.class.getName());

	public static List<UserDefinedParserParser> getParsers() {
		final List<UserDefinedParserParser> ret = Lists.newLinkedList();

		return ret;
	}
}
