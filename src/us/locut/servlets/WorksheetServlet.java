package us.locut.servlets;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.common.collect.*;

import us.locut.*;
import us.locut.db.*;
import us.locut.engines.*;
import us.locut.parsers.*;
import us.locut.parsers.UserDefinedParserParser.UserDefinedParser;
import us.locut.parsers.amounts.AmountMathOp;

import com.googlecode.objectify.Objectify;

@SuppressWarnings("serial")
public class WorksheetServlet extends HttpServlet {

	Set<String> recognizedWords = Sets.newHashSet();

	private ParserPickerFactory globalParserPickerFactory;

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
		parsers.add(new PreParser());
		parsers.addAll(AmountMathOp.getOps());
		parsers.add(new UserDefinedParserParser());
		globalParserPickerFactory = new RecentFirstParserPickerFactory(parsers);
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
					worksheet.qaPairs.add(new QAPair(e.getValue(), null));
				}
			}
			// Remove any qaPairs that have been removed from the browser DOM
			if (!orderedQuestions.isEmpty()) {
				while (worksheet.qaPairs.size() > orderedQuestions.lastKey()) {
					worksheet.qaPairs.remove(worksheet.qaPairs.size() - 1);
				}
			}
		}

		// Recompute worksheet
		final FixedOrderParserPickerFactory userDefinedParsers = new FixedOrderParserPickerFactory();
		final CombinedParserPickerFactory ppf = new CombinedParserPickerFactory(userDefinedParsers,
				globalParserPickerFactory);
		final BacktrackingParseEngine parseEngine = new BacktrackingParseEngine(ppf);
		final Map<String, Integer> userDefinedKeywords = Maps.newHashMap();
		int pos = 0;
		for (final QAPair qap : worksheet.qaPairs) {
			if (qap.question.trim().length() == 0) {
				qap.answer = Lists.newArrayList();
			} else {
				if (qap.answer == null) {
					final ParserContext context = new ParserContext(parseEngine, 2000);
					qap.answer = parseEngine.parseAndGetLastStep(Parsers.tokenize(qap.question), context);
				}

				if (qap.answer.size() == 1 && qap.answer.get(0) instanceof UserDefinedParser) {
					final UserDefinedParser udp = (UserDefinedParser) qap.answer.get(0);

					if (udp.getTemplate().size() == 1 && udp.getTemplate().get(0) instanceof String) {
						final String keyword = (String) udp.getTemplate().get(0);
						if (!PreParser.reserved.contains(keyword) && !userDefinedKeywords.containsKey(keyword)) {
							userDefinedKeywords.put(keyword, pos + 1);
						}
					}
					userDefinedParsers.addParser(udp);
				}
			}
			pos++;
		}

		obj.put(worksheet);

		response.answers = Maps.newHashMap();
		response.variables = userDefinedKeywords;

		for (int x = 0; x < worksheet.qaPairs.size(); x++) {
			response.answers.put(x + 1, Renderers.toHtml(req.getRequestURI(), worksheet.qaPairs.get(x).answer)
					.toString());
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
