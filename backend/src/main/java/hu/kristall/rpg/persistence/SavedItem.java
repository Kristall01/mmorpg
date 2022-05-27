package hu.kristall.rpg.persistence;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SavedItem {
	
	public final String material;
	public final String type;
	public final List<String> description;
	
	public SavedItem(String material, String type, List<String> description) {
		this.type = type;
		this.material = material;
		this.description = Collections.unmodifiableList(description);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof SavedItem)) return false;
		SavedItem savedItem = (SavedItem) o;
		return type.equals(savedItem.type);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type);
	}
}
