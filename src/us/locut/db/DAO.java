package us.locut.db;

import com.googlecode.objectify.*;

public class DAO {

	static {
		ObjectifyService.register(Worksheet.class);
	}

	public static Objectify begin() {
		return ObjectifyService.begin();
	}
}
