package hu.kristall.rpg.persistence;

import java.util.Map;

public class SavedMerchantProduct {
	
	private String productType;
	private Map<String, Integer> price;
	private int productAmount;
	
	public SavedMerchantProduct(String productType, Map<String, Integer> price, int productAmount) {
		this.productType = productType;
		this.price = price;
		this.productAmount = productAmount;
	}
	
	public String getProductType() {
		return productType;
	}
	
	public Map<String, Integer> getPrice() {
		return price;
	}
	
	public int getProductAmount() {
		return productAmount;
	}
}
