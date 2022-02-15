package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutFollowEntity extends PacketOut {
	
	int id;
	
	public PacketOutFollowEntity(int entityID) {
		super("followentity");
		this.id = entityID;
	}
	
	public PacketOutFollowEntity(Entity e) {
		this(e.getID());
	}
}
