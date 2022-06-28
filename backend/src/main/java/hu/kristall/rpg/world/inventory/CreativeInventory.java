package hu.kristall.rpg.world.inventory;

import hu.kristall.rpg.ItemMap;
import hu.kristall.rpg.lang.Lang;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.network.packet.out.PacketOutSound;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.item.ItemGenerator;

import java.util.HashMap;
import java.util.Map;

public class CreativeInventory extends Inventory {
	
	private ItemMap itemMap;
	private Lang lang;
	
	public static CreativeInventory createCreativeInventory(ItemMap itemMap, Lang lang) {
		Map<Item, Integer> items = new HashMap<>();
		for (String itemType : itemMap.getItemTypes()) {
			ItemGenerator generator = itemMap.getItem(itemType);
			items.put(generator.generateItem(), 1);
		}
		return new CreativeInventory(items, itemMap, lang);
	}
	
	private CreativeInventory(Map<Item, Integer> items, ItemMap itemMap, Lang lang) {
		super(items);
		this.itemMap = itemMap;
		this.lang = lang;
	}
	
	@Override
	public void interactItem(EntityHuman interacter, String type) {
		interacter.getInventory().addItem(itemMap.getItem(type).generateItem(), 1);
		interacter.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutSound("ball"));
		interacter.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat(lang.getMessage("merchant.item-purchased")));
	}
	
	@Override
	public String getID() {
		return "creative";
	}
}
