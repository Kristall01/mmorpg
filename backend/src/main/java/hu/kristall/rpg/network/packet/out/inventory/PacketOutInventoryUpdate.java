package hu.kristall.rpg.network.packet.out.inventory;

import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.world.Inventory;

public class PacketOutInventoryUpdate extends PacketOut {

	private PacketOutInventoryUpdate(Inventory inventory) {
		super("update-inventory");
		//TODO implement
	}
	
}
