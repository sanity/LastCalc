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

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Unindex;

import javax.persistence.Id;

@Cache
public class User {
	public static final String passwordSalt = "5pDTVGeciETDWwy";

	@Id
	public long id;

	public String cookieId;

	public String username;

	public String email;

	@Unindex
	public String saltedPasswordHash;
}
