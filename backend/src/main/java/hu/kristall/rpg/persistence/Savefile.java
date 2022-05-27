package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.ItemMap;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;
import hu.kristall.rpg.world.item.SimpleItemGenerator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Savefile {
	
	public final Map<String, SavedLevel> levels;
	public final String defaultLevel;
	public final ItemMap itemMap;
	
	public Savefile(Map<String, SavedLevel> levels, String defaultLevel, ItemMap itemMap) {
		this.levels = Map.copyOf(levels);
		this.defaultLevel = defaultLevel;
		this.itemMap = itemMap;
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
				
				ItemMap.Builder builder = new ItemMap.Builder();
				
				String defaultLevel = base.get("default").getAsString();
				JsonObject itemsJson = base.getAsJsonObject("items");
				for (String itemType : itemsJson.keySet()) {
					JsonObject itemJson = itemsJson.get(itemType).getAsJsonObject();
					String material = itemJson.get("material").getAsString();
					List<String> description;
					JsonElement descriptionJson = itemJson.get("description");
					if(descriptionJson != null) {
						JsonArray descriptionJsonArray = descriptionJson.getAsJsonArray();
						description = new ArrayList<>(descriptionJsonArray.size());
						for (JsonElement element : descriptionJson.getAsJsonArray()) {
							description.add(element.getAsString());
						}
					}
					else {
						description = new ArrayList<>();
					}
					Material m = Material.valueOf(material);
					builder.registerItem(itemType, new SimpleItemGenerator(itemType, m, description));
				}
				return new Savefile(levels, defaultLevel, builder.bake());
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
		
	}
	
}