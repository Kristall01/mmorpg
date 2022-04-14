package hu.kristall.rpg.world.entity.cozy;

import com.google.gson.*;
import hu.kristall.rpg.ThreadCloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClothPack implements ThreadCloneable<ClothPack> {
	
	private List<ColoredCloth> clothes;
	private JsonElement serializedJson;
	
	public static final ClothPack naked = new ClothPack(Cloth.NO_BOTTOM, Cloth.NO_TOP, Cloth.NO_SHOES);
	public static final ClothPack suit = new ClothPack(Cloth.SUIT, Cloth.PANTS_SUIT, Cloth.SHOES);
	
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
				obj.addProperty("color", cloth.color.name());
				obj.addProperty("type", cloth.cloth.name());
				arr.add(obj);
			}
		}
		this.serializedJson = arr;
	}
	
	public ClothPack(Cloth... clothes) {
		this.clothes = new ArrayList<>(clothes.length);
		ColoredCloth[] c = new ColoredCloth[clothes.length];
		for (int i = 0; i < c.length; i++) {
			c[i] = new ColoredCloth(clothes[i], ClothColor.BLACK);
		}
		init(List.of(c));
	}
	
	@Override
	public ClothPack structuredClone() {
		return this;
	}
	public JsonElement serializeJson() {
		return this.serializedJson.deepCopy();
	}
	
	public static class SavedClothpackParser implements JsonDeserializer<ClothPack>, JsonSerializer<ClothPack> {
		
		private Logger logger = LoggerFactory.getLogger("ClothPackSerializer");
		
		@Override
		public ClothPack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			int i = 0;
			JsonArray elements = json.getAsJsonArray();
			ColoredCloth[] cc = new ColoredCloth[elements.size()];
			for (JsonElement element : elements) {
				JsonObject clothObj = element.getAsJsonObject();
				String clothID = clothObj.get("type").getAsString();
				String clothColorID = clothObj.get("color").getAsString();
				Cloth cloth;
				try {
					cloth = Cloth.valueOf(clothID);
				}
				catch (IllegalArgumentException ex) {
					logger.warn("Failed to parse unknown cloth type '"+clothID+'\'');
					return ClothPack.naked;
				}
				ClothColor clothColor;
				try {
					clothColor = ClothColor.valueOf(clothColorID);
				}
				catch (IllegalArgumentException ex) {
					logger.warn("Failed to parse unknown cloth color type '"+clothColorID+'\'');
					return ClothPack.naked;
				}
				cc[i++] = new ColoredCloth(cloth, clothColor);
			}
			return new ClothPack(cc);
		}
		
		@Override
		public JsonElement serialize(ClothPack src, Type typeOfSrc, JsonSerializationContext context) {
			return src.serializeJson();
		}
	}
}
