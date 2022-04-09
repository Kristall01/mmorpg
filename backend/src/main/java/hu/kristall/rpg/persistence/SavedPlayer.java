package hu.kristall.rpg.persistence;

import com.google.gson.*;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.entity.EntityHuman;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SavedPlayer {
	
	public final String name;
	public final String world;
	public final Position pos;
	public final double hp;
	public final List<SavedItemStack> inventory;
	public final List<String> clothes;
	
	public SavedPlayer(String name, String world, Position pos, double hp, List<SavedItemStack> inventory, List<String> clothes) {
		this.name = name;
		this.world = world;
		this.pos = pos;
		this.hp = hp;
		this.inventory = List.copyOf(inventory);
		this.clothes = List.copyOf(clothes);
	}
	
	public SavedPlayer(EntityHuman entityHuman) {
		this.name = entityHuman.getName();
		this.world = entityHuman.getWorld().getName();
		this.pos = entityHuman.getPosition();
		this.hp = entityHuman.getHp();
		this.inventory = entityHuman.getInventory().structuredClone();
		this.clothes = entityHuman.getClothes().structuredClone();
	}
	
	public static class SavedPlayerPersistence implements JsonDeserializer<SavedPlayer> {
		
		@Override
		public SavedPlayer deserialize(JsonElement jsonElement, Type jsontype, JsonDeserializationContext ctx) throws JsonParseException {
			try {
				JsonObject base = jsonElement.getAsJsonObject();
				String name = base.get("name").getAsString();
				String world = base.get("world").getAsString();
				Position pos = ctx.deserialize(base.get("pos"), Position.class);
				double hp = base.get("hp").getAsDouble();
				JsonArray inventory = base.get("inventory").getAsJsonArray();
				List<SavedItemStack> items = new ArrayList<>();
				for (JsonElement item : inventory) {
					items.add(ctx.deserialize(item, SavedItemStack.SavedItemStackPersistence.class));
				}
				List<String> clothes = new ArrayList<>();
				for (JsonElement cloth : base.get("clothes").getAsJsonArray()) {
					clothes.add(cloth.getAsString());
				}
				return new SavedPlayer(name, world, pos, hp, items, clothes);
			}
			catch (Throwable err) {
				err.printStackTrace();
				throw err;
			}
		}
		
	}
	
}
