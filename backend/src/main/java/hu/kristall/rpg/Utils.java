package hu.kristall.rpg;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	private static final Gson gson;
	public static final Runnable emptyRunnable = () -> {};
	
	static {
		GsonBuilder builder = new GsonBuilder();
/*		builder.registerTypeAdapter(SavedLevel.class, new SavedLevel.SavedLevelParser());
		builder.registerTypeAdapter(Position.class, Parsers.positionParser);
		builder.registerTypeAdapter(SavedPortal.class, new SavedPortal.SavedPortalParser());
		builder.registerTypeAdapter(SavedItem.class, new SavedItem.SavedItemPersistence());
		builder.registerTypeAdapter(SavedMonsterspawn.class, new SavedMonsterspawn.SavedMonsterspawnPersistence());
		builder.registerTypeAdapter(SavedItemStack.class, new SavedItemStack.SavedItemStackPersistence());*/
		
		gson = builder.create();
	}
	
	public static <T> List<T> mapJsonArray(JsonElement e, Type type, JsonDeserializationContext ctx) {
		JsonArray arr = e.getAsJsonArray();
		ArrayList<T> elements = new ArrayList<>();
		for (JsonElement portalJson : arr) {
			elements.add(ctx.deserialize(portalJson, type));
		}
		return elements;
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
