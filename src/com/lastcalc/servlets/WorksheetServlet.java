package com.lastcalc.servlets;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.common.collect.Maps;

import com.googlecode.objectify.Objectify;
import com.lastcalc.*;
import com.lastcalc.db.*;
import com.lastcalc.parsers.PreParser;
import com.lastcalc.parsers.currency.Currencies;

@SuppressWarnings("serial")
public class WorksheetServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
		// Force initialization of SequentialParser static fields
		SequentialParser.create();
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final WorksheetRequest request = Misc.gson.fromJson(req.getReader(), WorksheetRequest.class);
		final WorksheetResponse response = new WorksheetResponse();
		if (request.getRecognizedWords) {
			response.recognizedWords = SequentialParser.recognizedWords;
		}
		final Objectify obj = DAO.begin();
		final Worksheet worksheet = obj.find(Worksheet.class, request.worksheetId);
		if (worksheet == null) {
			resp.sendError(404);
			return;
		}
		final ArrayList<QAPair> qaPairs = worksheet.qaPairs;
		if (request.questions != null) {
			int earliestModified = Integer.MAX_VALUE;
			final TreeMap<Integer, String> orderedQuestions = Maps.newTreeMap();
			orderedQuestions.putAll(request.questions);
			for (final Entry<Integer, String> e : orderedQuestions.entrySet()) {
				final int pos = e.getKey() - 1;
				if (pos < qaPairs.size()) {
					final QAPair qaPair = qaPairs.get(pos);
					qaPair.question = e.getValue();
					earliestModified = Math.min(earliestModified, pos);
				} else {
					qaPairs.add(new QAPair(e.getValue(), null));
				}
			}
			for (int x = earliestModified; x < qaPairs.size(); x++) {
				final QAPair qaPair = qaPairs.get(x);
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
		for (final QAPair qap : qaPairs) {
			if (qap.question.trim().length() == 0) {
				qap.answer = TokenList.createD();
			} else {
				if (qap.answer == null) {
					qap.answer = seqParser.parseNext(Parsers.tokenize(qap.question));
				} else {
					seqParser.processNextAnswer(qap.answer);
				}
			}
		}

		obj.put(worksheet);

		response.answers = Maps.newHashMap();
		response.variables = seqParser.getUserDefinedKeywordMap();

		for (int x = 0; x < qaPairs.size(); x++) {
			response.answers.put(x + 1,
					Renderers.toHtml(req.getRequestURI(), PreParser.flatten(qaPairs.get(x).answer))
					.toString());
		}

		resp.setContentType("application/json");
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

		Map<String, Integer> variables;
	}
}
