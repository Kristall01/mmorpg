package hu.kristall.rpg.network.packet.out;


import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;

public class PacketOutSpawnEntity extends PacketOut {
	
	double x, y, speed;
	int ID;
	String type;
	
	public PacketOutSpawnEntity(int entityID, double speed, String type, Position startPosition) {
		super("spawnentity");
		this.x = startPosition.getX();
		this.y = startPosition.getY();
		this.speed = speed;
		this.type = type;
		this.ID = entityID;
	}
	
	public PacketOutSpawnEntity(Entity entity) {
		this(entity.getID(), entity.getSpeed(), entity.type().name(), entity.getPosition());
	}
	
}
