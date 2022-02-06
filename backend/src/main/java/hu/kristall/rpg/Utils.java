package hu.kristall.rpg;

import com.google.gson.Gson;

public class Utils {
	
	private static final Gson gson;
	public static final Runnable emptyRunnable = () -> {};
	
	static {
		gson = new Gson();
	}
	
	public static Gson gson() {
		return gson;
	}
	
	public static String toJson(Object o) {
		return gson.toJson(o);
	}
	
	public static String[] fsplit(String input, char c) {
		if(input.length() == 0)
			return new String[] {input};
		char[] array = input.toCharArray();
		int delimiters = 0;
		for(int i = 0; i < array.length; ++i) {
			if(array[i] == c)
				++delimiters;
		}
		if(delimiters == 0)
			return new String[] {input};
		String[] returned = new String[delimiters+1];
		int start = 0;
		int count = 0;
		for(int i = 0; i < array.length; ++i) {
			if(array[i] == c) {
				returned[count++] = new String(array, start, i-start);
				start = i+1;
			}
		}
		returned[count++] = new String(array, start, array.length-start);
		return returned;
	}
	
}
