package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOutEntityRename;
import hu.kristall.rpg.network.packet.out.PacketOutEntityspeed;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.Path;

public abstract class Entity {
	
	private int entityID;
	private EntityType type;
	private final World world;
	private double speed;
	private boolean removed = false;
	private String name;
	
	public Entity(World world, EntityType type, int entityID, double speed) {
		this.type = type;
		this.world = world;
		this.entityID = entityID;
		this.speed = speed;
	}
	
	public abstract Position getPosition();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		world.broadcastPacket(new PacketOutEntityRename(this));
	}
	
	public EntityType type() {
		return type;
	}
	
	public int getID() {
		return entityID;
	}
	
	public World getWorld() {
		return world;
	}
	
	public abstract Path getLastPath();
	
	public abstract void move(Position to);
	
	public double getSpeed() {
		return this.speed;
	}
	
	public void remove() {
		this.removed = true;
		world.cleanRemovedEntity(this);
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void setSpeed(double newSpeed) {
		this.speed = newSpeed;
		this.world.broadcastPacket(new PacketOutEntityspeed(this));
		this.move(getLastPath().getTarget());
	}
	
}
