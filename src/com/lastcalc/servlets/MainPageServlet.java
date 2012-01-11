package com.lastcalc.servlets;

import java.net.URL;

import javax.servlet.http.HttpServlet;

import org.jsoup.nodes.*;

import com.googlecode.objectify.Objectify;
import com.lastcalc.Renderers;
import com.lastcalc.db.*;

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
				// This is readonly, dupilcate it and redirect to
				// a new id
				final Worksheet worksheet = new Worksheet();

				final Worksheet template = obj.query(Worksheet.class).filter("readOnlyId", worksheetId).get();

				worksheet.parentId = worksheet.id;

				worksheet.qaPairs = template.qaPairs;

				obj.put(worksheet);

				resp.sendRedirect("/" + worksheet.id);
			} else {

				final Worksheet worksheet = obj.get(Worksheet.class, worksheetId);

				if (worksheet == null) {
					resp.sendError(404);
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
				int x = 1;
				for (final QAPair qa : worksheet.qaPairs) {
					final Element question = doc.body().appendElement("div").attr("class", "question");
					question.appendElement("div").attr("class", "question_no").text(Integer.toString(x));
					question.appendElement("div").attr("class", "editable").attr("contentEditable", "true")
					.text(qa.question);
					question.appendElement("div").attr("class", "answer")
					.html(Renderers.toHtml(requestURL.toString(), qa.answer).toString());
					x++;
				}
				final Element question = doc.body().appendElement("div").attr("class", "question");
				question.appendElement("div").attr("class", "question_no").text(Integer.toString(x));
				question.appendElement("div").attr("class", "editable").attr("contentEditable", "true");
				question.appendElement("div").attr("class", "answer");
				resp.setContentType("text/html; charset=UTF-8");
				resp.getWriter().append(doc.toString());
			}
		};

	}
}
