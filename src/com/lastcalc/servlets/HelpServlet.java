package com.lastcalc.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.lastcalc.lessons.Help;

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
		resp.getWriter().append(Help.getHelpDoc().toString());
	}
}
