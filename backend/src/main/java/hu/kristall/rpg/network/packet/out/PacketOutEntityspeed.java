package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutEntityspeed extends PacketOut {
	
	int id;
	double speed;
	
	public PacketOutEntityspeed(int entityID, double speed) {
		super("entityspeed");
		this.id = entityID;
		this.speed = speed;
	}
	
	public PacketOutEntityspeed(Entity entity) {
		this(entity.getID(), entity.getSpeed());
	}
	
}
