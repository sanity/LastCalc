package com.lastcalc.servlets;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.lastcalc.lessons.*;
import com.lastcalc.lessons.Lessons.Lesson;

public class LessonServlet extends HttpServlet {
	private static final long serialVersionUID = -7706942563225877578L;

	@Override
	public void init() throws javax.servlet.ServletException {
		// Force static initialization
		Lessons.lessons.size();
	};

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
	IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	public static class LessonRequest {
		public Set<String> completedLessons;

		public String lessonId;
	}

	public static class LessonResponse {
		public Lesson lesson;

		public List<String> next;
	}
}
