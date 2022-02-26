package hu.kristall.rpg.world.entity.cozy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClothPack {
	
	private List<String> serialized;
	private List<Cloth> clothes;
	
	public ClothPack(Cloth... clothes) {
		byte b = 0;
		for (Cloth cloth : clothes) {
			b = (byte)(b ^ cloth.bitmap);
		}
		if(b != 7) {
			throw new IllegalArgumentException("illegal combination");
		}
		this.clothes = List.of(clothes);
		List<String> names = new ArrayList<>(3);
		for (Cloth cloth : clothes) {
			if(!cloth.transparent) {
				names.add(cloth.name());
			}
		}
		this.serialized = names;
	}
	
	public List<String> serialize() {
		return Collections.unmodifiableList(this.serialized);
	}
	
}
