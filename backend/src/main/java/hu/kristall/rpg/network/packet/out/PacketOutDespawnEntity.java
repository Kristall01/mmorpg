package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutDespawnEntity extends PacketOut {
	
	int id;
	
	public PacketOutDespawnEntity(Entity e) {
		super("despawnentity");
		this.id = e.getID();
	}
	
}
