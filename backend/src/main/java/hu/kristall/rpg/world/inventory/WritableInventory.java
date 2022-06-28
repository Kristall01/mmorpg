package hu.kristall.rpg.world.inventory;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.entity.Entity;

import java.util.Map;

public abstract class WritableInventory extends Inventory {
	
	private Entity owner;
	private boolean broadcastStopped = false;
	
	public WritableInventory(Entity owner, Map<Item, Integer> items) {
		super(items);
		this.owner = owner;
	}
	
	public void broadcastUpdate() {
		if(this.owner != null && !broadcastStopped) {
			owner.setInventory(this);
		}
	}
	
	public void addItem(Item item, int amount) {
		items.merge(item, amount, Integer::sum);
		broadcastUpdate();
	}
	
	public boolean removeItemsAtomically(Map<Item, Integer> removeCandidates) {
		for (Map.Entry<Item, Integer> entry : removeCandidates.entrySet()) {
			if(countItems(entry.getKey()) < entry.getValue()) {
				return false;
			}
		}
		for (Map.Entry<Item, Integer> itemIntegerEntry : removeCandidates.entrySet()) {
			int value = items.get(itemIntegerEntry.getKey());
			if(value == itemIntegerEntry.getValue()) {
				items.remove(itemIntegerEntry.getKey());
			}
			else {
				items.put(itemIntegerEntry.getKey(), value - itemIntegerEntry.getValue());
			}
		}
		broadcastUpdate();
		return true;
	}
	
	public int removeItem(Item item, int amount) {
		Integer i = items.get(item);
		if(i == null) {
			return 0;
		}
		if(i == amount) {
			items.remove(item);
			broadcastUpdate();
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
	
}
