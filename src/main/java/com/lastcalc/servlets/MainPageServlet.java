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

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.lastcalc.Misc;
import com.lastcalc.Renderers;
import com.lastcalc.SequentialParser;
import com.lastcalc.TokenList;
import com.lastcalc.db.DAO;
import com.lastcalc.db.Line;
import com.lastcalc.db.Worksheet;
import com.lastcalc.lessons.Help;
import com.lastcalc.servlets.WorksheetServlet.AnswerType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import javax.servlet.http.HttpServlet;
import java.net.URL;

public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = -797244922688131805L;

	@Override
	protected void doGet(final javax.servlet.http.HttpServletRequest req,
			final javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException,
			java.io.IOException {
		final boolean skipUACheck = req.getParameterMap().containsKey("skipuacheck");
		if (!skipUACheck && req.getHeader("User-Agent").contains("MSIE")) {
			resp.sendRedirect("/noie.html");
			return;
		}

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

			obj.save().entity(worksheet).now();

			resp.sendRedirect("/" + worksheet.id + (skipUACheck ? "?skipuacheck=1" : ""));
		} else {

			final String worksheetId = path.substring(1);

			if (worksheetId.length() == 8) {
				// This is readonly, duplicate it and redirect to
				// a new id
				final Worksheet worksheet = new Worksheet();

				final Worksheet template = obj.load().type(Worksheet.class).filter("readOnlyId", worksheetId).first().get();

				if (template == null) {
					resp.sendError(404);
					return;
				}

				worksheet.parentId = worksheet.id;

				worksheet.qaPairs = template.qaPairs;

				obj.save().entity(worksheet);

				resp.sendRedirect("/" + worksheet.id);
			} else {
				final Worksheet worksheet;
				try {
					worksheet = obj.load().type(Worksheet.class).id(worksheetId).get();
				} catch (final NotFoundException e) {
					resp.sendError(404, "Worksheet not found");
					return;
				}

                final Document doc = createDocument(requestURL, worksheet);

				// doc.body().appendElement("iframe").attr("id",
				// "helpframe").attr("src", "/help")
				// .attr("frameBorder", "0");

				final Element helpDiv = doc.body().appendElement("div").attr("id", "helpframe")
						.attr("style", "display: none;");
				for (final Node n : Help.getHelpDoc().body().childNodes()) {
					helpDiv.appendChild(n.clone());
				}

				int lineNo = 1;
				final SequentialParser sp = SequentialParser.create();
                Element worksheetElement = doc.body().select("#worksheet").first();
				for (final Line qa : worksheet.qaPairs) {
					sp.processNextAnswer(qa.answer);
					final Element lineEl = worksheetElement.appendElement("div").addClass("line")
							.attr("id", "line" + lineNo);
					if (lineNo == 1) {
						lineEl.addClass("firstLine");
					}
					
					final Element lineNumber=lineEl.appendElement("div").attr("class","lineNumberMarker");
					lineNumber.text(lineNo+".");
					
					final Element question = lineEl.appendElement("div").attr("class", "question")
							.attr("contentEditable", "true");
					question.text(qa.question);
					final TokenList strippedAnswer = sp.stripUDF(qa.answer);
					final AnswerType aType = WorksheetServlet.getAnswerType(strippedAnswer);
					if (aType.equals(AnswerType.NORMAL)) {
						lineEl.appendElement("div").attr("class", "equals").text("=");
						lineEl.appendElement("div").attr("class", "answer")
						.html(Renderers.toHtml("/", strippedAnswer).toString());
					} else {
						lineEl.appendElement("div").attr("class", "equals")
						.html("<span style=\"font-size:10pt;\">&#10003</span>");
						lineEl.appendElement("div").attr("class", "answer");
					}
					lineNo++;
				}
				doc.body().attr("data-variables", Misc.gson.toJson(sp.getUserDefinedKeywordMap()));
				final Element lineEl = worksheetElement.appendElement("div").addClass("line").attr("id", "line" + lineNo);
				if (lineNo == 1) {
					lineEl.addClass("firstLine");
				}
				final Element lineNumber=lineEl.appendElement("div").attr("class","lineNumberMarker");
				lineNumber.text(lineNo+".");
				final Element question = lineEl.appendElement("div").attr("class", "question")
						.attr("contentEditable", "true");
				final Element equals = lineEl.appendElement("div").attr("class", "equals").text("=")
						.attr("style", "display:none;");
				lineEl.appendElement("div").attr("class", "answer").attr("style", "display:none;");
				resp.setContentType("text/html; charset=UTF-8");
				resp.getWriter().append(doc.toString());
			}
		}

	}

    private Document createDocument(final URL requestURL, final Worksheet worksheet) {
        final Document doc = Document.createShell(requestURL.toString());
        doc.head().appendElement("title").text("LastCalc");
        doc.head().appendElement("link").attr("rel", "stylesheet").attr("href", "/css/highlighting.css")
        .attr("type", "text/css");
        doc.head().appendElement("link").attr("rel", "stylesheet").attr("href", "/css/locutus.css")
        .attr("type", "text/css");
        doc.head().appendElement("link").attr("rel", "stylesheet")
        .attr("href", "http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/themes/base/jquery-ui.css")
        .attr("type", "text/css");
        doc.head().appendElement("script").attr("src", "/js/json2.js");
        doc.head().appendElement("script")
        .attr("src", "https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js");
        // doc.head().appendElement("script").attr("src",
        // "/js/jquery.tools.min.js");
        doc.head().appendElement("script")
        .attr("src", "https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/jquery-ui.min.js");
        doc.head().appendElement("script").attr("src", "/js/jquery.cookie.js");
        doc.head().appendElement("script").attr("src", "/js/rangy-core.js");
        doc.head().appendElement("script").attr("src", "/js/rangy-selectionsaverestore.js");
        doc.head().appendElement("script").attr("src", "/js/locutus.js");
        doc.head()
        .appendElement("script")
        .attr("type", "text/javascript")
        .text("function woopraReady(tracker) {tracker.setDomain('lastcalc.com');tracker.setIdleTimeout(300000);tracker.track();return false;}(function(){var wsc = document.createElement('script');wsc.src = document.location.protocol+'//static.woopra.com/js/woopra.js';wsc.type = 'text/javascript';wsc.async = true;var ssc = document.getElementsByTagName('script')[0];ssc.parentNode.insertBefore(wsc, ssc);})();");

        doc.head().append("<script type=\"text/javascript\"> var _gaq = _gaq || []; _gaq.push(['_setAccount', 'UA-354970-27']); _gaq.push(['_trackPageview']);(function() {var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);})();</script>");
        doc.body().attr("data-worksheet-id", worksheet.id);
        doc.body().attr("data-worksheet-ro-id", worksheet.readOnlyId);
        final Element header = doc.body().appendElement("div").attr("id", "header");
        header.appendElement("div").attr("id", "logo").text("LastCalc");
        header.appendElement("div").attr("id", "help-button").text("Show Help");
        final Element ws = doc.body().appendElement("div").attr("id", "worksheet");
        ws.appendElement("div").attr("class", "groups").appendElement("a")
        .attr("href", "https://github.com/sanity/LastCalc/wiki")
        .attr("target", "_blank").html("LastCalc is open source!  Read more...");
        return doc;
    }
}
