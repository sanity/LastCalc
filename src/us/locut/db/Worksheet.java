package us.locut.db;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.*;

import us.locut.Misc;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.*;

@Cached
public class Worksheet {

	private static final Logger log = Logger.getLogger(Worksheet.class.getName());

	public Worksheet() {
		id = Misc.randomString(7);
		qaPairs = Lists.newArrayList();

	}

	@Id
	public String id;

	@Serialized
	public ArrayList<QAPair> qaPairs;

	public Date lastModified;

	@SuppressWarnings("unused")
	@PrePersist
	private void prePersist() {
		lastModified = new Date();
	}
}
