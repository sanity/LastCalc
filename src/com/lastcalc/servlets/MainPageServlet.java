package com.lastcalc.servlets;

import java.net.URL;

import javax.servlet.http.HttpServlet;

import org.jsoup.nodes.*;

import com.googlecode.objectify.*;
import com.lastcalc.*;
import com.lastcalc.db.*;
import com.lastcalc.parsers.*;
import com.lastcalc.parsers.UserDefinedParserParser.UserDefinedParser;
import com.lastcalc.servlets.WorksheetServlet.AnswerType;

public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = -797244922688131805L;

	@Override
	protected void doGet(final javax.servlet.http.HttpServletRequest req,
			final javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException,
			java.io.IOException {
		final URL requestURL = new URL(req.getRequestURL().toString());

		final String path = requestURL.getPath();

		final Objectify obj = DAO.begin();

		if (path.equals("/favicon.ico")) {
			resp.sendError(404);
			return;
		}

		if (path.equals("/")) {
			// Create a new worksheet and redirect to it
			final Worksheet worksheet = new Worksheet();

			obj.put(worksheet);

			resp.sendRedirect("/" + worksheet.id);
		} else {

			final String worksheetId = path.substring(1);

			if (worksheetId.length() == 8) {
				// This is readonly, duplicate it and redirect to
				// a new id
				final Worksheet worksheet = new Worksheet();

				final Worksheet template = obj.query(Worksheet.class).filter("readOnlyId", worksheetId).get();

				if (template == null) {
					resp.sendError(404);
					return;
				}

				worksheet.parentId = worksheet.id;

				worksheet.qaPairs = template.qaPairs;

				obj.put(worksheet);

				resp.sendRedirect("/" + worksheet.id);
			} else {
				final Worksheet worksheet;
				try {
					worksheet = obj.get(Worksheet.class, worksheetId);
				} catch (final NotFoundException e) {
					resp.sendError(404, "Worksheet not found");
					return;
				}

				final Document doc = Document.createShell(requestURL.toString());
				doc.head().appendElement("title").text("LastCalc");
				doc.head().appendElement("link").attr("rel", "stylesheet").attr("href", "/css/highlighting.css")
				.attr("type", "text/css");
				doc.head().appendElement("link").attr("rel", "stylesheet").attr("href", "/css/locutus.css")
				.attr("type", "text/css");
				doc.head().appendElement("script")
				.attr("src", "https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js");
				doc.head().appendElement("script")
				.attr("src", "http://cdn.jquerytools.org/1.2.6/all/jquery.tools.min.js");
				doc.head().appendElement("script").attr("src", "/js/rangy-core.js");
				doc.head().appendElement("script").attr("src", "/js/rangy-selectionsaverestore.js");
				doc.head().appendElement("script").attr("src", "/js/locutus.js");
				doc.head()
				.appendElement("script")
				.attr("type", "text/javascript")
				.text("function woopraReady(tracker) {tracker.setDomain('lastcalc.com');tracker.setIdleTimeout(300000);tracker.track();return false;}(function(){var wsc = document.createElement('script');wsc.src = document.location.protocol+'//static.woopra.com/js/woopra.js';wsc.type = 'text/javascript';wsc.async = true;var ssc = document.getElementsByTagName('script')[0];ssc.parentNode.insertBefore(wsc, ssc);})();");
				doc.body().attr("data-worksheet-id", worksheet.id);
				doc.body().attr("data-worksheet-ro-id", worksheet.readOnlyId);
				final Element header = doc.body().appendElement("div").attr("id", "header");
				header.appendElement("h3").attr("id", "logo").text("LastCalc");

				doc.body().appendElement("div").attr("class", "groups").appendElement("a")
				.attr("href", "https://groups.google.com/forum/?hl=en#!forum/lastcalc")
				.html("Ideas, Feedback, Questions, or Problems?  <u>Sign up</u> for our Google Group");

				int lineNo = 1;
				final SequentialParser sp = SequentialParser.create();
				for (final Line qa : worksheet.qaPairs) {
					sp.processNextAnswer(qa.answer);
					final Element lineEl = doc.body().appendElement("div").addClass("line")
							.attr("id", "line" + lineNo);
					if (lineNo == 1) {
						lineEl.addClass("firstLine");
					}
					final Element question = lineEl.appendElement("div").attr("class", "question")
							.attr("contentEditable", "true");
					question.text(qa.question);
					AnswerType aType = null;
					String retAnswer = "";
					if (qa.answer.size() == 1 && (qa.answer.get(0) instanceof UserDefinedParser)) {
						final UserDefinedParser udp = (UserDefinedParser) qa.answer.get(0);
						if (udp.hasVariables()) {
							aType = AnswerType.FUNCTION;
						} else {
							final TokenList udfResult = udp.after;
							// This is slightly naughty as the SeqParser won't
							// be in
							// exactly
							// the same state as it was when the UDF was parsed.
							// Unlikely to
							// cause problems though (fingers crossed!).
							final TokenList parsedUdfResult = sp.quietParse(udfResult);

							if (parsedUdfResult.size() == 1) {
								retAnswer = Renderers.toHtml(req.getRequestURI(), PreParser.flatten(parsedUdfResult))
										.toString();
								aType = AnswerType.NORMAL;
							} else {
								aType = AnswerType.FUNCTION;
							}
						}
					} else {
						aType = AnswerType.NORMAL;
						retAnswer = Renderers.toHtml(req.getRequestURI(), PreParser.flatten(qa.answer))
								.toString();
					}
					if (aType.equals(AnswerType.NORMAL)) {
						lineEl.appendElement("div").attr("class", "equals").text("=");
					} else {
						lineEl.appendElement("div").attr("class", "equals")
								.html("<span style=\"font-size:10pt;\">&#10003</span>");
					}
					lineEl.appendElement("div").attr("class", "answer").html(retAnswer);
					sp.processNextAnswer(qa.answer);
					lineNo++;
				}
				doc.body().attr("data-variables", Misc.gson.toJson(sp.getUserDefinedKeywordMap()));
				final Element lineEl = doc.body().appendElement("div").addClass("line").attr("id", "line" + lineNo);
				if (lineNo == 1) {
					lineEl.addClass("firstLine");
				}
				final Element question = lineEl.appendElement("div").attr("class", "question")
						.attr("contentEditable", "true");
				final Element equals = lineEl.appendElement("div").attr("class", "equals").text("=")
						.attr("style", "display:none;");
				lineEl.appendElement("div").attr("class", "answer").attr("style", "display:none;");
				resp.setContentType("text/html; charset=UTF-8");
				resp.getWriter().append(doc.toString());
			}
		};

	}
}
