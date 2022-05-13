package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutDespawnEntity extends PacketOut {
	
	int id;
	
	private PacketOutDespawnEntity() {
		super("despawnentity");
	}
	
	public PacketOutDespawnEntity(int entityID) {
		this();
		this.id = entityID;
	}
	
	public PacketOutDespawnEntity(Entity entity) {
		this(entity.getID());
	}
	
}
