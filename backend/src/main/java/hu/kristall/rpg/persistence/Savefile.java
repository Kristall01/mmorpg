package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.ItemMap;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.world.Material;
import hu.kristall.rpg.world.PotionEffectType;
import hu.kristall.rpg.world.item.InteractHandler;
import hu.kristall.rpg.world.item.ItemFlags;
import hu.kristall.rpg.world.item.SimpleItemGenerator;
import hu.kristall.rpg.world.item.interact.DefencePotion;
import hu.kristall.rpg.world.item.interact.HealthPotion;
import hu.kristall.rpg.world.item.interact.StrengthPotion;

import java.lang.reflect.Type;
import java.util.*;

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
					String name = itemJson.get("name").getAsString();
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
					ItemFlags flags;
					JsonElement jsonFlagsJson = itemJson.get("flags");
					
					JsonElement potion = itemJson.get("potion");
					List<InteractHandler> interactHandlers = new ArrayList<>();
					if(potion != null) {
						JsonElement healthElement = potion.getAsJsonObject().get("health");
						if(healthElement != null) {
							interactHandlers.add(new HealthPotion(healthElement.getAsDouble()));
						}
						JsonElement strengthElement = potion.getAsJsonObject().get(PotionEffectType.STRENGTH.name().toLowerCase());
						if(strengthElement != null) {
							JsonObject obj = strengthElement.getAsJsonObject();
							interactHandlers.add(new StrengthPotion(obj.get("lasts").getAsLong(), obj.get("amount").getAsDouble()));
						}
						JsonElement defenceElement = potion.getAsJsonObject().get(PotionEffectType.DEFENCE.name().toLowerCase());
						if(defenceElement != null) {
							JsonObject obj = defenceElement.getAsJsonObject();
							interactHandlers.add(new DefencePotion(obj.get("lasts").getAsLong(), obj.get("amount").getAsDouble()));
						}
					}
					
					if(jsonFlagsJson != null) {
						flags = Utils.gson().fromJson(jsonFlagsJson, ItemFlags.class);
					}
					else {
						flags = new ItemFlags();
					}
					builder.registerItem(itemType, new SimpleItemGenerator(itemType, m, name, description, flags, interactHandlers));
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