package hu.kristall.rpg.world;

import hu.kristall.rpg.world.entity.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
	
	private Map<Item, Integer> items = new HashMap<>();
	private Entity owner;
	private boolean broadcastStopped = false;
	
	public Inventory(Entity owner) {
		this.owner = owner;
	}
	
	private void broadcastUpdate() {
		if(this.owner != null && !broadcastStopped) {
			owner.setInventory(this);
		}
	}
	
	public boolean hasItem(Item item) {
		return items.containsKey(item);
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public void addItem(Item item, int amount) {
		items.merge(item, amount, Integer::sum);
		broadcastUpdate();
	}
	
	public int removeItem(Item item, int amount) {
		Integer i = items.get(item);
		if(i == null) {
			return 0;
		}
		if(i == amount) {
			items.remove(item);
			return amount;
		}
		int min = Math.min(amount, i);
		items.put(item, i - amount);
		broadcastUpdate();
		return min;
	}
	
	public void addAll(Inventory other) {
		broadcastStopped = true;
		for (Map.Entry<Item, Integer> entry : other.items.entrySet()) {
			addItem(entry.getKey(), entry.getValue());
		}
		this.broadcastStopped = false;
		this.broadcastUpdate();
	}
	
	public Collection<Map.Entry<Item, Integer>> getItems() {
		return Collections.unmodifiableCollection(items.entrySet());
	}
}
