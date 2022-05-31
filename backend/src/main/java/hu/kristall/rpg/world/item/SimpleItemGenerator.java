package hu.kristall.rpg.world.item;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;

import java.util.List;

public class SimpleItemGenerator implements ItemGenerator {
	
	private String type;
	private Material material;
	private List<String> description;
	private ItemFlags flags;
	
	public SimpleItemGenerator(String type, Material material, List<String> description, ItemFlags flags) {
		this.type = type;
		this.material = material;
		this.description = description;
		this.flags = flags;
	}
	
	@Override
	public Item get() {
		return new Item(type, material, description, flags);
	}
	
}
