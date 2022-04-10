package hu.kristall.rpg.persistence;

import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.List;

public class SavedPlayer {
	
	public final String name;
	public final LogoutPosition logoutPosition;
	public final double hp;
	public final List<SavedItemStack> inventory;
	public final List<String> clothes;
	public final boolean loaded;
	
	public SavedPlayer(String name, LogoutPosition pos, double hp, List<SavedItemStack> inventory, List<String> clothes) {
		this.loaded = true;
		this.name = name;
		this.logoutPosition = pos;
		this.hp = hp;
		this.inventory = List.copyOf(inventory);
		this.clothes = List.copyOf(clothes);
	}
	
	public SavedPlayer(EntityHuman entityHuman) {
		this.loaded = false;
		this.name = entityHuman.getName();
		this.logoutPosition = new LogoutPosition(entityHuman.getPosition(), entityHuman.getWorld().getName());
		this.hp = entityHuman.getHp();
		this.inventory = entityHuman.getInventory().structuredClone();
		this.clothes = entityHuman.getClothes().structuredClone();
	}
	
	/*public static class SavedPlayerPersistence implements JsonDeserializer<SavedPlayer> {
		
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
		
	}*/
	
}
