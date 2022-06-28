package hu.kristall.rpg.network.packet.out.inventory;

import hu.kristall.rpg.network.packet.out.PacketOut;

public class PacketOutCloseInventory extends PacketOut {
	public PacketOutCloseInventory() {
		super("close-inventory");
	}
}
