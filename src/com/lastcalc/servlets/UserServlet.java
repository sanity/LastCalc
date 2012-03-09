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

import javax.servlet.http.HttpServlet;

public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 8713053995956225568L;

	@Override
	protected void doPost(final javax.servlet.http.HttpServletRequest req,
			final javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException,
			java.io.IOException {
	}

	public static class UserServletRequest {
		public String username, saltedPasswordHash;

		public RegisterRequest register;
	}

	public static class RegisterRequest {
		public String email;
	}

}
