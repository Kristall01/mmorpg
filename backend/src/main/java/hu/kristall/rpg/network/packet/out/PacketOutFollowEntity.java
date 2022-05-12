package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutFollowEntity extends PacketOut {
	
	int id;
	
	private PacketOutFollowEntity() {
		super("followentity");
	}
	
	public PacketOutFollowEntity(int entityID) {
		this();
		this.id = entityID;
	}
	
	public PacketOutFollowEntity(Entity e) {
		this(e.getID());
	}
}
