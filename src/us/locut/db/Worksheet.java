package us.locut.db;

import javax.persistence.Id;

import com.googlecode.objectify.Key;

public class Worksheet {

	@Id
	public String id;

	public Key<QAPair>[] qas;

}
