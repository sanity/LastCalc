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
import com.lastcalc.parsers.bool.IfThenElse;

public class SequentialParser implements Serializable {
	private static final Logger log = Logger.getLogger(Bootstrap.class.getName());

	public static final Set<String> recognizedWords = Sets.newHashSet();
	public static RecentFirstParserPickerFactory globalParserPickerFactory;
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
		globalParserPickerFactory = new RecentFirstParserPickerFactory(allParsers);

		// Recompute worksheet
		// The first thing we do is parse any datastructures like lists or maps
		priorityParsers.addParser(new PreParser());
		priorityParsers.addParser(new IfThenElse());
		// Next we want to parse any user-defined functions before subsequent
		// parsers screw them up
		lowPriorityParsers.addParser(new UserDefinedParserParser());

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
					log.warning("Failed to parse line" + lineNo + " as UserDefinedParserParser (" + next + ")");
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
				: Integer.MAX_VALUE;
		return new SequentialParser(priorityParsers, globalParserPickerFactory, lowPriorityParsers, timeout);
	}

	private static final long serialVersionUID = 3602019924032548636L;
	private final FixedOrderParserPickerFactory userDefinedParsers;
	private final BacktrackingParseEngine parseEngine;
	private final ParserContext context;
	private final Map<String, Integer> userDefinedKeywords;
	private int pos;

	private TokenList previousAnswer = null;

	private int lastParseStepCount;

	public SequentialParser(final ParserPickerFactory priorityParsers, final ParserPickerFactory allParsers,
			final ParserPickerFactory lowPriorityParsers,
			final long timeout) {

		userDefinedParsers = new FixedOrderParserPickerFactory();
		final CombinedParserPickerFactory ppf = new CombinedParserPickerFactory(priorityParsers, userDefinedParsers,
				allParsers, lowPriorityParsers);
		parseEngine = new BacktrackingParseEngine(ppf);
		context = new ParserContext(parseEngine, 2000);
		userDefinedKeywords = Maps.newHashMap();
	}

	public TokenList parseNext(final String question) {
		return parseNext(Tokenizer.tokenize(question));
	}

	public TokenList parseNext(final TokenList question) {
		TokenList answer = null;
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
}
