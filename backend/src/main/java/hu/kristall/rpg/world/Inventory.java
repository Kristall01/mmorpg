package hu.kristall.rpg.world;

import hu.kristall.rpg.ThreadCloneable;
import hu.kristall.rpg.persistence.SavedItemStack;
import hu.kristall.rpg.world.entity.Entity;

import java.util.*;

public class Inventory implements ThreadCloneable<List<SavedItemStack>> {
	
	private Map<Item, Integer> items = new HashMap<>();
	private Entity owner;
	private boolean broadcastStopped = false;
	
	public Inventory(Entity owner) {
		this.owner = owner;
	}
	
	public Inventory(Entity owner, Map<Item, Integer> items) {
		this.owner = owner;
		this.items = items;
	}
	
	public void broadcastUpdate() {
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
	
	public void setBroadcastStopped(boolean broadcastStopped) {
		this.broadcastStopped = broadcastStopped;
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
	
	@Override
	public List<SavedItemStack> structuredClone() {
		List<SavedItemStack> l = new ArrayList<>(this.items.size());
		for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
			l.add(new SavedItemStack(entry.getValue(), entry.getKey().structuredClone()));
		}
		return l;
	}
	
}
