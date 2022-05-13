package hu.kristall.rpg.persistence;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Savefile {
	
	public final Map<String, SavedLevel> levels;
	public final String defaultLevel;
	
	public Savefile(Map<String, SavedLevel> levels, String defaultLevel) {
		this.levels = Map.copyOf(levels);
		this.defaultLevel = defaultLevel;
	}
	
	public static class SavefilePersistence implements JsonDeserializer<Savefile> {
		
		@Override
		public Savefile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				Map<String, SavedLevel> levels = new HashMap<>();
				JsonObject levelsObject = base.get("levels").getAsJsonObject();
				for (String s : levelsObject.keySet()) {
					SavedLevel level = ctx.deserialize(levelsObject.get(s), SavedLevel.class);
					level.name = s;
					levels.put(s, level);
				}
				
				String defaultLevel = base.get("default").getAsString();
				return new Savefile(levels, defaultLevel);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
		
	}
	
}