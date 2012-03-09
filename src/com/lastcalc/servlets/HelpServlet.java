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
