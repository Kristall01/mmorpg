package hu.kristall.rpg.network.packet.out;


import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;

public class PacketOutSpawnEntity extends PacketOut {
	
	double x, y, speed, hp, maxHp;
	int ID;
	String type;
	
	private PacketOutSpawnEntity() {
		super("spawnentity");
	}
	
	public PacketOutSpawnEntity(int entityID, double speed, String type, Position startPosition, double hp, double maxHP) {
		this();
		this.x = startPosition.getX();
		this.y = startPosition.getY();
		this.speed = speed;
		this.type = type;
		this.ID = entityID;
		this.hp = hp;
		this.maxHp = maxHP;
	}
	
	public PacketOutSpawnEntity(Entity entity) {
		this(entity.getID(),
			entity.getSpeed(),
			entity.type().name(),
			entity.getPosition(),
			entity.getHp(),
			entity.getMaxHp()
		);
	}
	
}
