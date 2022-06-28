package hu.kristall.rpg.network.packet.out.inventory;

import hu.kristall.rpg.network.packet.out.PacketOut;

public class PacketOutOpenInventory extends PacketOut {

	String inventoryID;
	
	private PacketOutOpenInventory() {
		super("open-inventory");
	}
	
	public PacketOutOpenInventory(String inventoryID) {
		this();
		this.inventoryID = inventoryID;
	}
	
}
