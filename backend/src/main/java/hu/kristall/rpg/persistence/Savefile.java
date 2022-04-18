package hu.kristall.rpg.persistence;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Savefile {
	
	public final Map<String, SavedLevel> levels;
	public final Map<String, SavedPlayer> players;
	public final String defaultLevel;
	
	public Savefile(Map<String, SavedLevel> levels, Map<String, SavedPlayer> players, String defaultLevel) {
		this.levels = Map.copyOf(levels);
		this.players = Map.copyOf(players);
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
					levels.put(s, level);
				}
				
				Map<String, SavedPlayer> players = new HashMap<>();
				JsonObject playersObject = base.get("players").getAsJsonObject();
				for (String s : playersObject.keySet()) {
					SavedPlayer player = ctx.deserialize(playersObject.get(s), SavedPlayer.class);
					players.put(s, player);
				}
				String defaultLevel = base.get("defaultLevel").getAsString();
				return new Savefile(levels, players, defaultLevel);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
		
	}
	
}