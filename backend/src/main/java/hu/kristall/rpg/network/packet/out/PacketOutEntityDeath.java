package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutEntityDeath extends PacketOut {

	int id;
	
	private PacketOutEntityDeath() {
		super("entityDeath");
	}
	
	public PacketOutEntityDeath(int entityID) {
		this();
		this.id = entityID;
	}
	
	public PacketOutEntityDeath(Entity entity) {
		this(entity.getID());
	}
	
}
