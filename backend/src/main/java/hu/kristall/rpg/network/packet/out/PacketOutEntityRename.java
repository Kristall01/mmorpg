package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutEntityRename extends PacketOut {
	
	String newname;
	int id;
	
	public PacketOutEntityRename(int entityID, String newname) {
		super("entityrename");
		this.id = entityID;
		this.newname = newname;
	}
	
	public PacketOutEntityRename(Entity entity) {
		this(entity.getID(), entity.getName());
	}
}
