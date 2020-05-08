package utils;

import java.util.HashMap;

public class AddMap {
	
	private AddMap() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static void sumValuesInMap(HashMap<String, Integer> map, String key, int inc) {
		Integer actual = map.get(key);
		if (actual == null)
			map.put(key, inc);
		else
			map.put(key, actual + inc);
	}
	
	public static int getValuesInMap(HashMap<String, Integer> map, String key) {
		Integer value = map.get(key);
		if (value == null)
			return 0;
		else
			return value;
	}
}
