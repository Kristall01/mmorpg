package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutEntityDeath extends PacketOut {

	int id;
	
	public PacketOutEntityDeath(int entityID) {
		super("entityDeath");
		this.id = entityID;
	}
	
	public PacketOutEntityDeath(Entity entity) {
		this(entity.getID());
	}
	
}
