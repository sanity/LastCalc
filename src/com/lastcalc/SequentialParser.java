package com.lastcalc;

import java.io.Serializable;
import java.util.*;

import com.google.common.collect.*;

import com.google.appengine.api.utils.SystemProperty;
import com.lastcalc.engines.*;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.parsers.amounts.AmountMathOp;

public class SequentialParser implements Serializable {
	private static final Set<String> recognizedWords = Sets.newHashSet();
	private static RecentFirstParserPickerFactory globalParserPickerFactory;;
	private static final FixedOrderParserPickerFactory priorityParsers = new FixedOrderParserPickerFactory();
	static {
		final LinkedList<Parser> allParsers = Lists.newLinkedList();
		com.lastcalc.Parsers.getAll(allParsers);
		for (final Parser p : allParsers) {
			for (final Object i : p.getTemplate()) {
				if (i instanceof String) {
					recognizedWords.add((String) i);
				}
			}
		}
		allParsers.addAll(AmountMathOp.getOps());
		globalParserPickerFactory = new RecentFirstParserPickerFactory(allParsers);

		// Recompute worksheet
		// The first thing we do is parse any datastructures like lists or maps
		priorityParsers.addParser(new PreParser());
		// Next we want to parse any user-defined functions before subsequent
		// parsers screw them up
		priorityParsers.addParser(new UserDefinedParserParser());
	}

	public static SequentialParser create() {
		final long timeout = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production ? 2000
				: Integer.MAX_VALUE;
		return new SequentialParser(priorityParsers, globalParserPickerFactory, timeout);
	}

	private static final long serialVersionUID = 3602019924032548636L;
	private final FixedOrderParserPickerFactory userDefinedParsers;
	private final BacktrackingParseEngine parseEngine;
	private final ParserContext context;
	private final Map<String, Integer> userDefinedKeywords;
	private int pos;

	public SequentialParser(final ParserPickerFactory priorityParsers, final ParserPickerFactory allParsers,
			final long timeout) {

		userDefinedParsers = new FixedOrderParserPickerFactory();
		final CombinedParserPickerFactory ppf = new CombinedParserPickerFactory(priorityParsers, userDefinedParsers,
				allParsers);
		parseEngine = new BacktrackingParseEngine(ppf);
		context = new ParserContext(parseEngine, 2000);
		userDefinedKeywords = Maps.newHashMap();
	}

	public TokenList parseNext(final String question) {
		return parseNext(Parsers.tokenize(question));
	}

	public TokenList parseNext(final TokenList question) {
		final TokenList answer = parseEngine.parseAndGetLastStep(question, context);
		processNextAnswer(answer);
		return answer;
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
		pos++;
	}

	public Map<String, Integer> getUserDefinedKeywordMap() {
		return userDefinedKeywords;
	}
}
