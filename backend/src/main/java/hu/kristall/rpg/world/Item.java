package hu.kristall.rpg.world;

import hu.kristall.rpg.ThreadCloneable;
import hu.kristall.rpg.persistence.SavedItem;

import java.util.Objects;

public class Item implements ThreadCloneable<SavedItem> {
	
	private Material type;
	private String name;
	
	public Item(Material type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Item(Material type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Material getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Item)) return false;
		Item item = (Item) o;
		return getType().equals(item.getType()) && Objects.equals(getName(), item.getName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getType(), getName());
	}
	
	@Override
	public SavedItem structuredClone() {
		return new SavedItem(type.name(), this.name);
	}
	
}
