package us.locut.servlets;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import us.locut.engines.*;
import us.locut.parsers.Parser;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

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
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
	}

	public static class WorksheetRequest {
		Map<Integer, String> questions;
	}

	public static class WorksheetResponse {
		Map<Integer, String> answers;
	}
}
