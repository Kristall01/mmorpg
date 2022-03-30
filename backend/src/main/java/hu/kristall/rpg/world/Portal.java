package hu.kristall.rpg.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;

public class Portal {
	
	private String targetWorldName;
	private Position position;
	private double radius;
	
	public Portal(Position position, String targetWorld) {
		this.position = position;
		this.targetWorldName = targetWorld;
		this.radius = 0.5;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public String getTargetWorldName() {
		return targetWorldName;
	}
	
	public boolean checkCollision(Entity e) {
		Position ePos = e.getPosition();
		return Position.distance(ePos, this.position) < radius;
	}
	
	public Position getPosition() {
		return this.position;
	}
	
}
