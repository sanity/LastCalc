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

	@Override
	public void init() throws ServletException {
		final LinkedList<Parser> parsers = Lists.newLinkedList();
		us.locut.Parsers.getAll(parsers);
		final ParserPickerFactory ppf = new RecentFirstParserPickerFactory(parsers);
		parseEngine = new BacktrackingParseEngine(ppf);
	}

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		final Objectify obj = DAO.begin();
		final WorksheetRequest request = Misc.gson.fromJson(req.getReader(), WorksheetRequest.class);
		final Worksheet worksheet = obj.find(Worksheet.class, request.worksheetId);
		if (worksheet == null) {
			resp.sendError(404);
			return;
		}
		int maxPos = 0;
		for (final Entry<Integer, String> e : request.questions.entrySet()) {
			final int pos = e.getKey() - 1;
			maxPos = Math.max(pos, maxPos);
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
		while (worksheet.qaPairs.size() > maxPos + 1) {
			worksheet.qaPairs.remove(worksheet.qaPairs.size() - 1);
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

		final WorksheetResponse response = new WorksheetResponse();
		response.answers = Maps.newHashMap();
		response.variables = variableDefinitions;

		for (int x = 0; x < worksheet.qaPairs.size(); x++) {
			response.answers.put(x + 1, Parsers.toHtml(worksheet.qaPairs.get(x).answer));
		}

		resp.setContentType("application/json");
		Misc.gson.toJson(response, resp.getWriter());
	}

	public static class WorksheetRequest {
		public String worksheetId;

		public Map<Integer, String> questions;
	}

	public static class WorksheetResponse {
		Map<Integer, String> answers;

		Map<String, Integer> variables;
	}
}
