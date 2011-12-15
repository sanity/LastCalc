package us.locut.servlets;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import us.locut.*;
import us.locut.Parsers.ParsedQuestion;
import us.locut.db.*;
import us.locut.engines.*;
import us.locut.parsers.*;

import com.google.appengine.repackaged.com.google.common.collect.*;
import com.googlecode.objectify.Objectify;

@SuppressWarnings("serial")
public class WorksheetServlet extends HttpServlet {

	ParseEngine parseEngine;

	Set<String> recognizedWords = Sets.newHashSet();

	@Override
	public void init() throws ServletException {
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		us.locut.Parsers.getAll(parsers);
		for (final Parser p : parsers) {
			for (final Object i : p.getTemplate()) {
				if (i instanceof String) {
					recognizedWords.add((String) i);
				}
			}
		}
		final ParserPickerFactory ppf = new RecentFirstParserPickerFactory(parsers);
		parseEngine = new BacktrackingParseEngine(ppf);
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final WorksheetRequest request = Misc.gson.fromJson(req.getReader(), WorksheetRequest.class);
		final WorksheetResponse response = new WorksheetResponse();
		if (request.getRecognizedWords) {
			response.recognizedWords = recognizedWords;
		}
		final Objectify obj = DAO.begin();
		final Worksheet worksheet = obj.find(Worksheet.class, request.worksheetId);
		if (worksheet == null) {
			resp.sendError(404);
			return;
		}
		if (request.questions != null) {
			final TreeMap<Integer, String> orderedQuestions = Maps.newTreeMap();
			orderedQuestions.putAll(request.questions);
			for (final Entry<Integer, String> e : orderedQuestions.entrySet()) {
				final int pos = e.getKey() - 1;
				if (pos < worksheet.qaPairs.size()) {
					final QAPair qaPair = worksheet.qaPairs.get(pos);
					qaPair.question = e.getValue();
					qaPair.answer = null; // Set to null to indicate that it must be
					// recomputed
				} else {
					worksheet.qaPairs.add(pos, new QAPair(e.getValue(), null));
				}
			}
			// Remove any qaPairs that have been removed from the browser DOM
			if (!orderedQuestions.isEmpty()) {
				while (worksheet.qaPairs.size() > orderedQuestions.lastKey() + 1) {
					worksheet.qaPairs.remove(worksheet.qaPairs.size() - 1);
				}
			}
		}

		// Recompute worksheet
		final ParserContext context = new ParserContext(parseEngine, Long.MAX_VALUE);
		final Map<String, ArrayList<Object>> variables = Maps.newHashMap();
		final Map<String, Integer> variableDefinitions = Maps.newHashMap();
		int pos = 0;
		for (final QAPair qap : worksheet.qaPairs) {
			final ParsedQuestion pq = Parsers.parseQuestion(qap.question, variables);

			if (qap.answer == null) {
				qap.answer = parseEngine.parseAndGetLastStep(pq.question, context);
			}

			if (pq.variableAssignment != null) {
				variableDefinitions.put(pq.variableAssignment, pos + 1);
				variables.put(pq.variableAssignment, qap.answer);
			}
			pos++;
		}

		obj.put(worksheet);

		response.answers = Maps.newHashMap();
		response.variables = variableDefinitions;

		for (int x = 0; x < worksheet.qaPairs.size(); x++) {
			response.answers.put(x + 1, Parsers.toHtml(worksheet.qaPairs.get(x).answer));
		}

		resp.setContentType("application/json");
		Misc.gson.toJson(response, resp.getWriter());
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
