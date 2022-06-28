package hu.kristall.rpg.world;

public class ItemStack {
	
	private int amount;
	private Item item;
	
	public ItemStack(int amount, Item item) {
		this.amount = amount;
		this.item = item;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public Item getItem() {
		return item;
	}
	
}
