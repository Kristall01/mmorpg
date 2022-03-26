package hu.kristall.rpg.world;

import java.util.Objects;

public class Item {
	
	private String type;
	
	public Item(String name) {
		this.type = name;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Item)) return false;
		Item item = (Item) o;
		return getType().equals(item.getType());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getType());
	}
}
