package hu.kristall.rpg.world.entity.cozy;

import hu.kristall.rpg.ThreadCloneable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClothPack implements ThreadCloneable<List<String>> {
	
	private List<String> serialized;
	private List<Cloth> clothes;
	
	public static final ClothPack naked = ClothPack.unsafePack(Cloth.NO_BOTTOM, Cloth.NO_TOP, Cloth.NO_SHOES);
	public static final ClothPack suit = ClothPack.unsafePack(Cloth.SUIT, Cloth.PANTS_SUIT, Cloth.SHOES);
	
	private ClothPack(List<String> serialized, List<Cloth> clothes) {
		this.serialized = serialized;
		this.clothes = clothes;
	}
	
	public static ClothPack unsafePack(Cloth... clothesArray) {
		List<Cloth> clothes = List.of(clothesArray);
		List<String> names = new ArrayList<>(clothesArray.length);
		for (Cloth cloth : clothes) {
			if(!cloth.transparent) {
				names.add(cloth.name());
			}
		}
		return new ClothPack(names, clothes);
	}
	
	public static ClothPack validatedPack(Cloth... clothes) {
		byte b = 0;
		for (Cloth cloth : clothes) {
			b = (byte)(b ^ cloth.bitmap);
		}
		if(b != 7) {
			throw new IllegalArgumentException("illegal combination");
		}
		return unsafePack(clothes);
	}
	
	public List<String> serialize() {
		return Collections.unmodifiableList(this.serialized);
	}
	
	@Override
	public List<String> structuredClone() {
		return this.serialized;
	}
}
