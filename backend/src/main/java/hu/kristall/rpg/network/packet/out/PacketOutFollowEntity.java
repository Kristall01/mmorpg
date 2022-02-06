package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutFollowEntity extends PacketOut {
	
	int id;
	
	public PacketOutFollowEntity(Entity e) {
		super("followentity");
		
		this.id = e.getID();
	}
}
