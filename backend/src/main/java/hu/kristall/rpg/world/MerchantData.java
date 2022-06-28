package hu.kristall.rpg.world;

import hu.kristall.rpg.ItemMap;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.persistence.SavedMerchantProduct;
import hu.kristall.rpg.world.entity.cozy.ClothPack;
import hu.kristall.rpg.world.inventory.MerchantProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantData {
	
	private Position position;
	private String ID, name;
	private List<SavedMerchantProduct> products;
	private ClothPack clothPack;
	
	public MerchantData(Position position, String ID, String name, List<SavedMerchantProduct> products, ClothPack coloredClothes) {
		this.position = position;
		this.ID = ID;
		this.products = products;
		this.name = name;
		this.clothPack = coloredClothes;
	}
	
	public String getID() {
		return ID;
	}
	
	public ClothPack getClothPack() {
		return clothPack;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public List<MerchantProduct> generateProductList(ItemMap itemMap) {
		ArrayList<MerchantProduct> realProducts = new ArrayList<>();
		for (SavedMerchantProduct product : products) {
			Map<Item, Integer> priceMap = new HashMap<>();
			for (Map.Entry<String, Integer> entry : product.getPrice().entrySet()) {
				priceMap.put(itemMap.getItem(entry.getKey()).generateItem(), entry.getValue());
			}
			realProducts.add(new MerchantProduct(itemMap.getItem(product.getProductType()), product.getProductAmount(), priceMap));
		}
		return realProducts;
	}
	
	public String getName() {
		return name;
	}
	
}
