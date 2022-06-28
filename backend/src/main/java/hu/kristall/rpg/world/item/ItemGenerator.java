package hu.kristall.rpg.world.item;

import hu.kristall.rpg.world.Item;

public interface ItemGenerator {
	
	Item generateItem();
	String itemType();

}
