package com.lastcalc.db;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.*;


import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.*;
import com.lastcalc.Misc;

@Cached
public class Worksheet {

	private static final Logger log = Logger.getLogger(Worksheet.class.getName());

	public Worksheet() {
		id = Misc.randomString(7);
		readOnlyId = Misc.randomString(8);
		qaPairs = Lists.newArrayList();

	}

	@Id
	public String id;

	public String parentId;

	public String readOnlyId;

	@Serialized
	public ArrayList<Line> qaPairs;

	public Date lastModified;

	@SuppressWarnings("unused")
	@PrePersist
	private void prePersist() {
		lastModified = new Date();
	}
}
