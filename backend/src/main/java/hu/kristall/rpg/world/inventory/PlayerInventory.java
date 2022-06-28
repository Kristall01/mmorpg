package hu.kristall.rpg.world.inventory;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.item.InteractHandler;

import java.util.Map;

public class PlayerInventory extends WritableInventory {
	
	public PlayerInventory(Entity owner, Map<Item, Integer> items) {
		super(owner, items);
	}
	
	@Override
	public void interactItem(EntityHuman interacter, String type) {
		Item candidateItem = null;
		for (Map.Entry<Item, Integer> item : getItems()) {
			candidateItem = item.getKey();
			if(candidateItem.getType().equals(type)) {
				break;
			}
		}
		if(candidateItem == null) {
			return;
		}
		boolean removeItem = false;
		for (InteractHandler interactHandler : candidateItem.getInteractHandlers()) {
			removeItem |= interactHandler.interact(interacter);
		}
		if(removeItem) {
			interacter.getInventory().removeItem(candidateItem, 1);
		}
	}
	
	@Override
	public String getID() {
		return "default";
	}
	
}
