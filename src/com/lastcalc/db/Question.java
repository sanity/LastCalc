package com.lastcalc.db;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Unindexed;

public class Question {
	@Id
	public long id;

	public int position;

	public String worksheet;

	@Unindexed
	public String question;
}
