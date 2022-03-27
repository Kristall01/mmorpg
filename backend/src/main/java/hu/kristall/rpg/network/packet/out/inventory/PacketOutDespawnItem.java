package hu.kristall.rpg.network.packet.out.inventory;

import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.world.FloatingItem;

public class PacketOutDespawnItem extends PacketOut {
	
	int id;
	
	private PacketOutDespawnItem() {
		super("despawn-item");
	}
	
	public PacketOutDespawnItem(int ID) {
		this();
		config(id);
	}
	
	public PacketOutDespawnItem(FloatingItem item) {
		this();
		config(item.getID().value);
	}
	
	private void config(int id) {
		this.id = id;
	}
	
}
