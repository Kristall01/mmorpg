package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedLevel {

	public final String name;
	public final int width, height;
	public final List<List<String>> layers;
	public final List<SavedMonsterspawn> monsterspawns;
	public final List<SavedPortal> portals;
	
	public SavedLevel(String name, int width, int height, List<List<String>> layers, List<SavedMonsterspawn> monsterspawns, List<SavedPortal> portals) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.layers = layers;
		this.monsterspawns = List.copyOf(monsterspawns);
		this.portals = List.copyOf(portals);
	}
	
	public static class SavedLevelParser implements JsonDeserializer<SavedLevel> {
		
		@Override
		public SavedLevel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				String name = base.get("name").getAsString();
				int width = base.get("width").getAsInt();
				int height = base.get("height").getAsInt();
				ArrayList<SavedPortal> portals = new ArrayList<>();
				for (JsonElement portalJson : base.get("portals").getAsJsonArray()) {
					portals.add(ctx.deserialize(portalJson, SavedPortal.class));
				}
				List<SavedMonsterspawn> monsters = Utils.mapJsonArray(base.get("monsterspawns"), SavedMonsterspawn.class, ctx);
				List<List<String>> layers = new ArrayList<>();
				for (JsonElement layer : base.get("layers").getAsJsonArray()) {
					Utils.mapJsonArray(layer, SavedLevel.class, ctx);
				}
				return new SavedLevel(name,width, height, layers,  monsters, portals);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
	}
	
}
