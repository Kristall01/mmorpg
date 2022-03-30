package hu.kristall.rpg.network.packet.out.inventory;

import com.google.gson.JsonObject;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.world.Inventory;
import hu.kristall.rpg.world.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PacketOutSetInventory extends PacketOut {
	
	List<JsonObject> items = new ArrayList<>();
	
	public PacketOutSetInventory(Inventory inventory) {
		super("setinventory");
		
		for (Map.Entry<Item, Integer> entry : inventory.getItems()) {
			JsonObject obj = new JsonObject();
			obj.addProperty("amount", entry.getValue());
			obj.add("item", Utils.gson().toJsonTree(entry.getKey()));
			items.add(obj);
		}
		
	}
	
	
}
