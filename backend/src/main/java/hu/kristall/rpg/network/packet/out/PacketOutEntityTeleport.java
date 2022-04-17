package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;

public class PacketOutEntityTeleport extends PacketOut {
	
	double x, y;
	int entityID;
	boolean instant;
	
	private PacketOutEntityTeleport() {
		super("teleport");
	}
	
	public PacketOutEntityTeleport(Entity entity, boolean instant) {
		this();
		Position pos = entity.getPosition();
		configure(pos.getX(), pos.getY(), entity.getID(), instant);
	}
	
	private void configure(double x, double y, int entityID, boolean instant) {
		this.x = x;
		this.y = y;
		this.entityID = entityID;
		this.instant = instant;
	}
	
	public PacketOutEntityTeleport(double x, double y, int entityID, boolean instant) {
		this();
		configure(x,y,entityID,instant);
	}
	
}