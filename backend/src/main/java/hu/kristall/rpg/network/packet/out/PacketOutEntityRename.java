package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.Entity;

public class PacketOutEntityRename extends PacketOut {
	
	String newname;
	int id;
	
	private PacketOutEntityRename() {
		super("entityrename");
	}
	
	public PacketOutEntityRename(int entityID, String newname) {
		this();
		this.id = entityID;
		this.newname = newname;
	}
	
	public PacketOutEntityRename(Entity entity) {
		this(entity.getID(), entity.getName());
	}
}
