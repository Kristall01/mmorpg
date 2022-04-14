package hu.kristall.rpg.persistence;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SavedItemStack {
	
	public final int amount;
	public final SavedItem item;
	
	public SavedItemStack(int amount, SavedItem item) {
		this.amount = amount;
		this.item = item;
	}
	
	
	public static class SavedItemStackPersistence implements JsonDeserializer<SavedItemStack> {
		
		@Override
		public SavedItemStack deserialize(JsonElement jsonElement, Type jsontype, JsonDeserializationContext ctx) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				int amount = base.get("amount").getAsInt();
				SavedItem i = ctx.deserialize(base.get("item"), SavedItem.class);
				return new SavedItemStack(amount, i);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
			
		}
	}
	
}
