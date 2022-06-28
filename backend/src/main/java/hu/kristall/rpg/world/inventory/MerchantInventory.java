package hu.kristall.rpg.world.inventory;

import hu.kristall.rpg.lang.Lang;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.network.packet.out.PacketOutSound;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutCloseInventory;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.*;

public class MerchantInventory extends Inventory {
	
	private Map<String, MerchantProduct> productMap;
	private String ID;
	private Lang lang;
	
	private MerchantInventory(Lang lang, String ID, Map<Item, Integer> items, Map<String, MerchantProduct> productMap) {
		super(items);
		this.ID = ID;
		this.productMap = productMap;
		this.lang = lang;
	}
	
	@Override
	public String getID() {
		return ID;
	}
	
	public static MerchantInventory createMerchantInventory(String ID, World world, List<MerchantProduct> products) {
		Lang lang = world.getAsyncServer().lang;
		int i = 0;
		
		Map<String, MerchantProduct> productMap = new HashMap<>();
		Map<Item, Integer> inventoryItems = new HashMap<>();
		
		for (MerchantProduct product : products) {
			Item item = product.getProduct().generateItem();
			List<String> shopItemDescription = new ArrayList<>(item.getDescription());
			if(shopItemDescription.size() != 0) {
				shopItemDescription.add("");
			}
			Set<Map.Entry<Item, Integer>> priceEntries = product.getPrice().entrySet();
			if(priceEntries.size() != 0) {
				shopItemDescription.add(lang.getMessage("merchant.price-list"));
				for (Map.Entry<Item, Integer> priceEntry : priceEntries) {
					shopItemDescription.add(lang.getMessage("merchant.price-tag", priceEntry.getKey().getName(), Integer.toString(priceEntry.getValue())));
					shopItemDescription.add("");
				}
			}
			else {
				shopItemDescription.add(lang.getMessage("merchant.price-free"));
			}
			shopItemDescription.add(lang.getMessage("merchant.item-footer"));
			
			String itemType = Integer.toString(i++);
			Item shopItem = new Item(itemType, item.getMaterial(), item.getName(), shopItemDescription, item.getFlags(), Collections.emptyList());
			inventoryItems.put(shopItem, product.getProductAmount());
			productMap.put(itemType, product);
		}
		return new MerchantInventory(lang, ID, inventoryItems, productMap);
	}
	
	@Override
	public void interactItem(EntityHuman interacter, String type) {
		MerchantProduct merchantProduct = productMap.get(type);
		if(merchantProduct == null) {
			return;
		}
		PlayerConnection connection = interacter.getWorldPlayer().getAsyncPlayer().connection;
		if(!interacter.getInventory().removeItemsAtomically(merchantProduct.getPrice())) {
			connection.sendPacket(new PacketOutSound("bling"));
			connection.sendPacket(new PacketOutChat(lang.getMessage("merchant.item-purchase-fail")));
			connection.sendPacket(new PacketOutCloseInventory());
			return;
		}
		interacter.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutSound("ball"));
		interacter.getInventory().addItem(merchantProduct.getProduct().generateItem(), merchantProduct.getProductAmount());
		interacter.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat(lang.getMessage("merchant.item-purchased")));
	}
	
}
