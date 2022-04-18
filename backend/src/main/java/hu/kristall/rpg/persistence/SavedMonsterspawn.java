package hu.kristall.rpg.persistence;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SavedMonsterspawn {

	public final int min, max;
	public final double playerRate;
	public final String type;
	public final long spawnCheckInterval;
	
	public SavedMonsterspawn(int min, int max, double playerRate, String type, long spawnCheckInterval) {
		this.min = min;
		this.max = max;
		this.playerRate = playerRate;
		this.type = type;
		this.spawnCheckInterval = spawnCheckInterval;
	}
	
	public static class SavedMonsterspawnPersistence implements JsonDeserializer<SavedMonsterspawn> {
		
		@Override
		public SavedMonsterspawn deserialize(JsonElement jsonElement, Type jsontype, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				int min = base.get("min").getAsInt();
				int max = base.get("max").getAsInt();
				double playerRate = base.get("playerRate").getAsDouble();
				String type = base.get("type").getAsString();
				long spawnCheckInterval = base.get("spawnCheckInterval").getAsLong();
				
				return new SavedMonsterspawn(min,max, playerRate, type, spawnCheckInterval);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
		
	}
	
}
