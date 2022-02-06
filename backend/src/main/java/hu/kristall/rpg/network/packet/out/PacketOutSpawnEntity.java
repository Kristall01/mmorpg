package hu.kristall.rpg.network.packet.out;


import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;

public class PacketOutSpawnEntity extends PacketOut {
	
	private double x, y, speed;
	private int ID;
	private String type;
	
	public PacketOutSpawnEntity(Entity entity) {
		super("spawnentity");
		Position p = entity.getPosition();
		this.x = p.getX();
		this.y = p.getY();
		this.speed = entity.getSpeed();
		this.type = entity.type().name();
		this.ID = entity.getID();
	}
	
}
