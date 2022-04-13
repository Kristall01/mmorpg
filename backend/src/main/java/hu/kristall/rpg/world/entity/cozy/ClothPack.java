package hu.kristall.rpg.world.entity.cozy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClothPack {
	
	private List<String> serialized;
	private List<ColoredCloth> clothes;
	private JsonElement serializedJson;
	
	public ClothPack(ColoredCloth... clothes) {
		init(List.of(clothes));
	}
	
	public ClothPack(List<ColoredCloth> clothes) {
		init(List.copyOf(clothes));
	}
	
	private void init(List<ColoredCloth> coloredCloths) {
		this.clothes = coloredCloths;
		
		JsonArray arr = new JsonArray();
		List<String> names = new ArrayList<>(3);
		for (ColoredCloth cloth : clothes) {
			if(!cloth.cloth.transparent) {
				JsonObject obj = new JsonObject();
				names.add(cloth.cloth.name());
				obj.addProperty("color", cloth.color.name());
				obj.addProperty("type", cloth.cloth.name());
				arr.add(obj);
			}
		}
		this.serializedJson = arr;
		this.serialized = names;
		
	}
	
	public ClothPack(Cloth... clothes) {
		this.clothes = new ArrayList<>(clothes.length);
		ColoredCloth[] c = new ColoredCloth[clothes.length];
		for (int i = 0; i < c.length; i++) {
			c[i] = new ColoredCloth(clothes[i], ClothColor.BLACK);
		}
		init(List.of(c));
	}
	
	public List<String> serialize() {
		return Collections.unmodifiableList(this.serialized);
	}
	
	public JsonElement serializeJson() {
		return this.serializedJson.deepCopy();
	}
	
}
