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
package com.lastcalc.servlets;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.common.collect.Maps;

import com.lastcalc.*;
import com.lastcalc.db.Line;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.parsers.currency.Currencies;

@SuppressWarnings("serial")
public class StatelessServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {

	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final WorksheetRequest request = Misc.gson.fromJson(req.getReader(), WorksheetRequest.class);
		final WorksheetResponse response = new WorksheetResponse();
		if (request.getRecognizedWords) {
			response.recognizedWords = SequentialParser.recognizedWords;
		}
		final ArrayList<Line> qaPairs = new ArrayList<Line>();
		if (request.questions != null) {
			int earliestModified = Integer.MAX_VALUE;
			final TreeMap<Integer, String> orderedQuestions = Maps.newTreeMap();
			orderedQuestions.putAll(request.questions);
			for (final Entry<Integer, String> e : orderedQuestions.entrySet()) {
				final int pos = e.getKey() - 1;
				if (pos < qaPairs.size()) {
					final Line qaPair = qaPairs.get(pos);
					qaPair.question = e.getValue();
					earliestModified = Math.min(earliestModified, pos);
				} else {
					qaPairs.add(new Line(e.getValue(), null));
				}
			}
			for (int x = earliestModified; x < qaPairs.size(); x++) {
				final Line qaPair = qaPairs.get(x);
				qaPair.answer = null;
			}
			// Remove any qaPairs that have been removed from the browser DOM
			if (!orderedQuestions.isEmpty()) {
				while (qaPairs.size() > orderedQuestions.lastKey()) {
					qaPairs.remove(qaPairs.size() - 1);
				}
			}
		}

		// Recompute worksheet
		final SequentialParser seqParser = SequentialParser.create();
		for (final Line qap : qaPairs) {
			if (qap.question.trim().length() == 0) {
				qap.answer = TokenList.createD();
			} else {
				if (qap.answer == null) {
					qap.answer = seqParser.parseNext(Tokenizer.tokenize(qap.question));
				} else {
					seqParser.processNextAnswer(qap.answer);
				}
			}
		}

		response.answers = Maps.newHashMap();
		response.answerTypes = Maps.newHashMap();
		response.variables = seqParser.getUserDefinedKeywordMap();

		for (int x = 0; x < qaPairs.size(); x++) {
			final TokenList answer = qaPairs.get(x).answer;
			final TokenList strippedAnswer = seqParser.stripUDF(answer);
			response.answers.put(x + 1, strippedAnswer.toString());
			response.answerTypes.put(x + 1, strippedAnswer.size() == 1
					&& strippedAnswer.get(0) instanceof UserDefinedParser ? AnswerType.FUNCTION
							: AnswerType.NORMAL);
		}

		resp.setContentType("application/json; charset=UTF-8");
		Misc.gson.toJson(response, resp.getWriter());

		if (Currencies.shouldUpdate()) {
			Currencies.updateExchangeRates();
		}
	}

	public static class WorksheetRequest {
		public boolean getRecognizedWords = false;

		public String worksheetId;

		public Map<Integer, String> questions;
	}

	public static class WorksheetResponse {
		Set<String> recognizedWords;

		Map<Integer, String> answers;

		Map<Integer, AnswerType> answerTypes;

		Map<String, Integer> variables;
	}

	public enum AnswerType {
		NORMAL, FUNCTION, ERROR
	}
}
