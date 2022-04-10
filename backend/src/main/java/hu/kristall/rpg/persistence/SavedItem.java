package hu.kristall.rpg.persistence;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

public class SavedItem {
	
	public final String type;
	public final String name;
	
	public SavedItem(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public static class SavedItemPersistence implements JsonDeserializer<SavedItem> {
		
		@Override
		public SavedItem deserialize(JsonElement jsonElement, Type jsontype, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				String type = base.get("type").getAsString();
				String name = base.get("name").getAsString();
				return new SavedItem(type, name);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
		
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof SavedItem)) return false;
		SavedItem savedItem = (SavedItem) o;
		return type.equals(savedItem.type) && Objects.equals(name, savedItem.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, name);
	}
}
