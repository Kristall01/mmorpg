package hu.kristall.rpg.network.packet.out.inventory;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.world.FloatingItem;

public class PacketOutSpawnItem extends PacketOut {
	
	int id;
	double x;
	double y;
	String type;
	
	private PacketOutSpawnItem() {
		super("spawn-item");
	}
	
	public PacketOutSpawnItem(FloatingItem item) {
		this();
		this.id = item.getID().value;
		Position pos = item.getPosition();
		this.x = pos.getX();
		this.y = pos.getY();
		this.type = item.getItem().getType();
		//TODO implement
	}
	
}
