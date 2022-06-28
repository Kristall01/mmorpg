package hu.kristall.rpg.world.inventory;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Inventory {
	
	protected Map<Item, Integer> items;
	
	public Inventory(Map<Item, Integer> items) {
		this.items = items;
	}
	
	public boolean hasItem(String itemType) {
		for (Item item : items.keySet()) {
			if(item.getType().equals(itemType)) {
				return true;
			}
		}
		return false;
	}
	
	public int countItems(Item type) {
		Integer i = items.get(type);
		if(i == null) {
			return 0;
		}
		return i;
	}
	
	public abstract void interactItem(EntityHuman interacter, String type);
	public abstract String getID();
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public Collection<Map.Entry<Item, Integer>> getItems() {
		return Collections.unmodifiableCollection(items.entrySet());
	}
	
	public Map<String, Integer> serialize() {
		Map<String, Integer> map = new HashMap<>();
		for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
			map.put(entry.getKey().getType(), entry.getValue());
		}
		return map;
	}
	
}
