package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;

public class PacketOutAttack extends PacketOut {
	
	int entityID;
	double x,y;
	
	private PacketOutAttack() {
		super("attack");
	}
	
	public PacketOutAttack(int entityID, double x, double y) {
		this();
		
		this.entityID = entityID;
		this.x = x;
		this.y = y;
	}
	
	public PacketOutAttack(Entity entity, Position pos) {
		this(entity.getID(), pos.getX(), pos.getY());
	}
	
}