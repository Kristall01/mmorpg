package hu.kristall.rpg.world.item;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;

import java.util.List;

public class SimpleItemGenerator implements ItemGenerator {
	
	private String type, name;
	private Material material;
	private List<String> description;
	private ItemFlags flags;
	private List<InteractHandler> interactHandlers;
	
	public SimpleItemGenerator(String type, Material material, String name, List<String> description, ItemFlags flags, List<InteractHandler> interactHandlers) {
		this.type = type;
		this.material = material;
		this.name = name;
		this.description = description;
		this.flags = flags;
		this.interactHandlers = interactHandlers;
	}
	
	@Override
	public Item generateItem() {
		return new Item(type, material, name, description, flags, interactHandlers);
	}
	
	@Override
	public String itemType() {
		return type;
	}
	
}
