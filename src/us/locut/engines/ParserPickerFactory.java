package us.locut.engines;

import java.io.Serializable;
import java.util.*;

public interface ParserPickerFactory extends Serializable {
	public ParserPicker getPicker();

	public void teach(List<ParseStep> step);

	public static interface ParserPicker {
		public ParseStep pickNext(ArrayList<Object> input);
	}
}
