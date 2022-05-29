package hu.kristall.rpg;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.item.ItemGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemMap {
	
	private Map<String, ItemGenerator> map;
	private ItemMap(Map<String, ItemGenerator> itemMap) {
		this.map = itemMap;
	}
	
	public ItemGenerator getItem(String type) {
		return map.get(type);
	}
	
	public static class Builder {
		private Map<String, ItemGenerator> map = new HashMap<>();
		
		public void registerItem(String type, ItemGenerator itemSupplier) {
			this.map.put(type, itemSupplier);
		}
		
		public ItemMap bake() {
			return new ItemMap(Map.copyOf(map));
		}
		
	}
	
	public Collection<String> getItemTypes() {
		return map.keySet();
	}
	
}
