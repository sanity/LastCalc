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

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import com.google.common.collect.*;

import org.jscience.mathematics.number.Number;
import org.jscience.physics.amount.Amount;

import com.google.appengine.api.utils.SystemProperty;
import com.lastcalc.bootstrap.Bootstrap;
import com.lastcalc.engines.*;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.parsers.bool.IfThenElseParser;

public class SequentialParser implements Serializable {
	private static final Logger log = Logger.getLogger(SequentialParser.class.getName());

	public static final Set<String> recognizedWords = Sets.newHashSet();
	public static ParserPickerFactory globalParserPickerFactory;
	private static final FixedOrderParserPickerFactory priorityParsers = new FixedOrderParserPickerFactory();
	private static final FixedOrderParserPickerFactory lowPriorityParsers = new FixedOrderParserPickerFactory();

	static {
		final LinkedList<Parser> allParsers = Lists.newLinkedList();
		com.lastcalc.parsers.Parser.getAll(allParsers);
		for (final Parser p : allParsers) {
			for (final Object i : p.getTemplate()) {
				if (i instanceof String) {
					recognizedWords.add((String) i);
				}
			}
		}
		recognizedWords.add("ans");
		// globalParserPickerFactory = new
		// RecentFirstParserPickerFactory(allParsers);
		globalParserPickerFactory = new KeywordParserPickerFactory(allParsers);

		// Recompute worksheet
		// The first thing we do is parse any datastructures like lists or maps
		priorityParsers.addParser(new PreParser());
		priorityParsers.addParser(new IfThenElseParser());
		priorityParsers.addParser(new UserDefinedParserParser());

		lowPriorityParsers.addParser(new ToLowerCase());

		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					Bootstrap.class.getResourceAsStream("bootstrap.txt")));
			final SequentialParser parser = SequentialParser.create();
			int lineNo = 1;
			while (true) {
				String next = br.readLine();
				if (next == null) {
					break;
				}
				next = next.trim();
				if (next.length() == 0 || next.startsWith("#")) {
					continue;
				}
				final TokenList parsed = parser.parseNext(next);
				if (parsed.size() != 1 || !(parsed.get(0) instanceof UserDefinedParser)) {
					log.warning("Failed to parse line " + lineNo + " as UserDefinedParserParser (" + next + " -> "
							+ parsed + ")");
				} else {
					globalParserPickerFactory.addParser((UserDefinedParser) parsed.get(0));
				}
				lineNo++;
			}
			br.close();

		} catch (final Exception e) {
			log.warning("Exception while loading boostrap parsers: " + e);
			e.printStackTrace();
		}
	}

	public static SequentialParser create() {
		final long timeout = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production ? 2000
				: 2000;
		return new SequentialParser(priorityParsers, globalParserPickerFactory, lowPriorityParsers, timeout);
	}

	public static SequentialParser create(final ParserContext pc) {
		final long timeout = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production ? 2000
				: 2000;
		return new SequentialParser(priorityParsers, globalParserPickerFactory, lowPriorityParsers, timeout, pc);
	}

	private static final long serialVersionUID = 3602019924032548636L;
	private final KeywordParserPickerFactory userDefinedParsers;
	private final BacktrackingParseEngine parseEngine;
	private final ParserContext context;
	private final Map<String, Integer> userDefinedKeywords = Maps.newConcurrentMap();
	private int pos;

	private TokenList previousAnswer = null;

	private int lastParseStepCount;

	public SequentialParser(final ParserPickerFactory priorityParsers, final ParserPickerFactory allParsers,
			final ParserPickerFactory lowPriorityParsers,
			final long timeout) {
		userDefinedParsers = new KeywordParserPickerFactory();
		final CombinedParserPickerFactory ppf = new CombinedParserPickerFactory(priorityParsers, userDefinedParsers,
				allParsers, lowPriorityParsers);
		parseEngine = new BacktrackingParseEngine(ppf);
		context = new ParserContext(parseEngine, timeout);
	}

	public SequentialParser(final ParserPickerFactory priorityParsers, final ParserPickerFactory allParsers,
			final ParserPickerFactory lowPriorityParsers,
			final long timeout, final ParserContext context) {

		userDefinedParsers = new KeywordParserPickerFactory();
		final CombinedParserPickerFactory ppf = new CombinedParserPickerFactory(priorityParsers, userDefinedParsers,
				allParsers, lowPriorityParsers);
		parseEngine = new BacktrackingParseEngine(ppf);
		this.context = context;
	}

	public TokenList parseNext(final String question) {
		return parseNext(Tokenizer.tokenize(question));
	}

	public TokenList parseNext(TokenList question) {
		TokenList answer = null;
		
		if(previousAnswer!=null){
			for(int i=0;i<question.size();i++){
				if(question.get(i).equals("ans")){
					question=question.replaceWithTokenList(i, i+1, previousAnswer);
				}
			}
		}
		
		if (previousAnswer != null && previousAnswer.size() == 1 && (previousAnswer.get(0) instanceof Amount || previousAnswer.get(0) instanceof Number)) {
			final TokenList questionWithPrevious = new TokenList.CompositeTokenList(previousAnswer, question);
			answer = parseEngine.parseAndGetLastStep(question, context, questionWithPrevious);
		} else {
			answer = parseEngine.parseAndGetLastStep(question, context);
		}
		lastParseStepCount = parseEngine.getLastParseStepCount();
		processNextAnswer(answer);

		return answer;
	}

	public TokenList stripUDF(final TokenList answer) {
		if (answer.size() == 1 && answer.get(0) instanceof UserDefinedParser) {
			final UserDefinedParser udf = (UserDefinedParser) answer.get(0);
			if (udf.variables.isEmpty()) {
				final TokenList parsedResult = parseEngine.parseAndGetLastStep(udf.after, context);
				if (parsedResult.size() == 1)
					return parsedResult;
			}
		}
		return answer;
	}

	// Parse a question without changing SequentialParser state
	public TokenList quietParse(final TokenList question) {
		return parseEngine.parseAndGetLastStep(question, context);
	}

	public void processNextAnswer(final TokenList answer) {
		if (answer.size() == 1 && answer.get(0) instanceof UserDefinedParser) {
			final UserDefinedParser udp = (UserDefinedParser) answer.get(0);

			if (udp.getTemplate().size() == 1 && udp.getTemplate().get(0) instanceof String) {
				final String keyword = (String) udp.getTemplate().get(0);
				if (!PreParser.reserved.contains(keyword) && !userDefinedKeywords.containsKey(keyword)) {
					userDefinedKeywords.put(keyword, pos + 1);
				}
			}
			userDefinedParsers.addParser(udp);
		}

		// We can also get a list of UDFs, for example from an import
		if (answer.size() == 1 && answer.get(0) instanceof Collection && ((Collection) answer.get(0)).size() > 0) {
			final List<UserDefinedParser> udfs = Lists.newArrayListWithCapacity(((Collection) answer.get(0)).size());
			// We do it this way to ensure that all items in the list are really
			// UserDefinedParsers
			for (final Object o : ((Collection<?>) answer.get(0))) {
				if (! (o instanceof UserDefinedParser)) {
					udfs.clear();
					break;
				} else {
					udfs.add((UserDefinedParser) o);
				}
			}
			for (final UserDefinedParser udf : udfs) {
				userDefinedParsers.addParser(udf);
			}
		}

		pos++;
		previousAnswer = answer;
	}

	public Map<String, Integer> getUserDefinedKeywordMap() {
		return userDefinedKeywords;
	}

	public void setDumpSteps(final boolean d) {
		parseEngine.setDumpSteps(d);
	}

	public int getLastParseStepCount() {
		return lastParseStepCount;
	}

	public ParserPickerFactory getUserDefinedParsers() {
		return userDefinedParsers;
	}
}
