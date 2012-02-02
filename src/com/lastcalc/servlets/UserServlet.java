package com.lastcalc.servlets;

import javax.servlet.http.HttpServlet;

public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 8713053995956225568L;

	@Override
	protected void doPost(final javax.servlet.http.HttpServletRequest req,
			final javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException,
			java.io.IOException {
	}

	public static class UserServletRequest {

	}

	public static class RegisterRequest {
		public String username, saltedPasswordHash, email;
	}
}
