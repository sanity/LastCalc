package com.lastcalc.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.utils.SystemProperty;
import com.lastcalc.lessons.Help;

import static com.google.appengine.api.utils.SystemProperty.environment;

public class HelpServlet extends HttpServlet {
	private static final long serialVersionUID = -7706942563225877578L;

	@Override
	public void init() throws javax.servlet.ServletException {
		// Force static initialization
		Help.lessons.size();
	};

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
	IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		final SystemProperty.Environment.Value env = environment.value();
		if (env == SystemProperty.Environment.Value.Production) {
			resp.getWriter().append(Help.helpDocAsString);
		} else {
			resp.getWriter().append(Help.createHelpDoc().toString());
		}
	}
}
