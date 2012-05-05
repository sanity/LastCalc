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
package com.lastcalc.db;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.*;

import com.google.common.collect.Lists;

import com.googlecode.objectify.annotation.*;
import com.lastcalc.Misc;
import com.lastcalc.parsers.Parser;

@Cached
public class Worksheet {
	private static final Logger log = Logger.getLogger(Worksheet.class.getName());

	public Worksheet() {
		id = Misc.randomString(7);
		readOnlyId = Misc.randomString(8);
		qaPairs = Lists.newArrayList();
		created = new Date();
	}

	@Id
	public String id;

	public String parentId;

	public String readOnlyId;

	public String description;

	public boolean used;

	@Serialized
	public ArrayList<Line> qaPairs;

	@Serialized
	public Collection<Parser> definedParsers;

	public Date created, lastModified;

	@SuppressWarnings("unused")
	@PrePersist
	private void prePersist() {
		lastModified = new Date();
		used = qaPairs.size() > 0;
	}
}
