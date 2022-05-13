package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutHpChange extends PacketOut {

	int id;
	double newHp;
	
	private PacketOutHpChange() {
		super("hpchange");
	}
	
	public PacketOutHpChange(int entityID, double newHp) {
		this();
		this.id = entityID;
		this.newHp = newHp;
	}
	
	public PacketOutHpChange(Entity e) {
		this(e.getID(), e.getHp());
	}
	
}
