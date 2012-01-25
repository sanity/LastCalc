package com.lastcalc.lessons;

import java.util.*;
import java.util.logging.Logger;

import com.google.common.collect.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class Lessons {
	private static final Logger log = Logger.getLogger(Lessons.class.getName());

	public static Map<String, Lesson> lessons;

	static {
		lessons = Maps.newHashMap();
		try {
			final Document tutorialDoc = Jsoup
					.parse(Lessons.class.getResourceAsStream("tutorial.html"), "UTF-8", null);
			for (final Element lessonElement : tutorialDoc.body().select("div.lesson")) {
				final Lesson lesson = new Lesson(lessonElement);
				lessons.put(lesson.id, lesson);
			}
		} catch (final Exception e) {
			log.warning("Exception while processing tutorial.html " + e);
		}
	}

	public static class Lesson {
		public Lesson(final Element lesson) {
			id = lesson.id();
			explanation = lesson.select("div.explanation").first();
			requires = Sets.newHashSet();
			for (final Element li : lesson.select("div.requires li")) {
				requires.add(li.text());
			}
		}

		public String id;

		public Element explanation;

		public Set<String> requires;
	}
}
