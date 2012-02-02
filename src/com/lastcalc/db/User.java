package com.lastcalc.db;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.*;

@Cached
public class User {
	public static final String passwordSalt = "5pDTVGeciETDWwy";

	@Id
	public long id;

	public String cookieId;

	public String username;

	public String email;

	@Unindexed
	public String saltedPasswordHash;
}
