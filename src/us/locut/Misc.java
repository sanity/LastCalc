package us.locut;

import java.text.NumberFormat;
import java.util.Random;

import com.google.gson.*;

public class Misc {
	public static Random rand = new Random();

	private static final String randStrChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz"
			+ "0123456789";

	public static String randomString(final int digits) {
		final StringBuilder sb = new StringBuilder(digits);
		for (int x = 0; x < digits; x++) {
			sb.append(randStrChars.charAt(rand.nextInt(randStrChars.length())));
		}
		return sb.toString();
	}

	public static final Gson gson = new GsonBuilder().create();

	public static NumberFormat numberFormat;
	static {
		numberFormat = NumberFormat.getInstance();
		numberFormat.setParseIntegerOnly(false);
	}
}
