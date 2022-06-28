package hu.kristall.rpg.world.inventory;

import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.item.ItemGenerator;

import java.util.Collections;
import java.util.Map;

public class MerchantProduct {
	private ItemGenerator product;
	private Map<Item, Integer> price;
	private int productAmount;
	
	public MerchantProduct(ItemGenerator product, int productAmount, Map<Item, Integer> price) {
		this.product = product;
		this.price = Collections.unmodifiableMap(price);
		this.productAmount = productAmount;
	}
	
	public ItemGenerator getProduct() {
		return product;
	}
	
	public Map<Item, Integer> getPrice() {
		return price;
	}
	
	public int getProductAmount() {
		return productAmount;
	}
	
}
