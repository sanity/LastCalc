package us.locut.engines;

import java.util.ArrayList;

public interface ParseEngine {

	public abstract ArrayList<Object> parse(final ArrayList<Object> input, final long maxTimeMillis);

}