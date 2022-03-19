package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutHpChange extends PacketOut {

	int id;
	double newHp;
	
	public PacketOutHpChange(int entityID, double newHp) {
		super("hpchange");
		this.id = entityID;
		this.newHp = newHp;
	}
	
	public PacketOutHpChange(Entity e) {
		this(e.getID(), e.getHp());
	}
	
}
