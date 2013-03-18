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

import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;
import com.lastcalc.Misc;
import com.lastcalc.parsers.Parser;

import javax.persistence.PrePersist;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Cache
@Entity
public class Worksheet {

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

	@Serialize
	public ArrayList<Line> qaPairs;

	@Serialize
	public Collection<Parser> definedParsers;

	public Date created, lastModified;

	@PrePersist
	private void prePersist() {
		lastModified = new Date();
		used = qaPairs.size() > 0;
	}
}
