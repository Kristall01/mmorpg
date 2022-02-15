package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutDespawnEntity extends PacketOut {
	
	int id;
	
	public PacketOutDespawnEntity(int entityID) {
		super("despawnentity");
		this.id = entityID;
	}
	
	public PacketOutDespawnEntity(Entity entity) {
		this(entity.getID());
	}
	
}
