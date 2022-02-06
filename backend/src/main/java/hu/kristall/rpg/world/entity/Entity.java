package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

public abstract class Entity {
	
	private int entityID;
	private EntityType type;
	private final World world;
	private double speed;
	
	public Entity(World world, EntityType type, int entityID, double speed) {
		this.type = type;
		this.world = world;
		this.entityID = entityID;
		this.speed = speed;
	}
	
	public abstract Position getPosition();
	
	public EntityType type() {
		return type;
	}
	
	public int getID() {
		return entityID;
	}
	
	public World getWorld() {
		return world;
	}
	
	public abstract void move(Position to);
	
	public double getSpeed() {
		return this.speed;
	}
	
}
