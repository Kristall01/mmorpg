package hu.kristall.rpg.world;

import hu.kristall.rpg.ThreadCloneable;
import hu.kristall.rpg.persistence.SavedItem;
import hu.kristall.rpg.world.item.InteractHandler;
import hu.kristall.rpg.world.item.ItemFlags;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Item implements ThreadCloneable<SavedItem> {
	
	private Material material;
	private String type;
	private List<String> description;
	private ItemFlags flags;
	private String name;
	private List<InteractHandler> interactHandlers;
	
	public Item(String type, Material material, String name, List<String> description, ItemFlags flags, List<InteractHandler> interactHandlers) {
		this.type = type;
		if(description == null) {
			description = Collections.emptyList();
		}
		this.name = name;
		this.description = Collections.unmodifiableList(description);
		this.material = material;
		if(flags == null) {
			flags = new ItemFlags();
		}
		this.flags = flags;
		this.interactHandlers = interactHandlers;
	}
	
	public String getName() {
		return name;
	}
	
	public Item(String type, String name, Material material) {
		this(type, material, name, null, null, Collections.emptyList());
	}
	
	public List<InteractHandler> getInteractHandlers() {
		return interactHandlers;
	}
	
	public ItemFlags getFlags() {
		return flags;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public List<String> getDescription() {
		return description;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Item)) return false;
		Item item = (Item) o;
		return type.equals(item.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type);
	}
	
	@Override
	public SavedItem structuredClone() {
		return new SavedItem(material.name(), type, this.description, flags);
	}
	
}
