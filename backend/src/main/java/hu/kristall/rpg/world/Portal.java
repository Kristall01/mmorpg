package hu.kristall.rpg.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.persistence.SavedPortal;
import hu.kristall.rpg.world.entity.Entity;

public class Portal {
	
	private String targetWorldName;
	private Position position, targetPosition;
	private double radius;
	
	public Portal(Position position, String targetWorld, Position targetPosition) {
		this.position = position;
		this.targetWorldName = targetWorld;
		this.targetPosition = targetPosition;
		this.radius = 1;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public String getTargetWorldName() {
		return targetWorldName;
	}
	
	public Position getTargetPosition() {
		return targetPosition;
	}
	
	public boolean checkCollision(Entity e) {
		Position ePos = e.getPosition();
		return Position.distance(ePos, this.position) < radius;
	}
	
	public Position getPosition() {
		return this.position;
	}
	
	public SavedPortal serialize() {
		return new SavedPortal(this.position, this.targetWorldName, this.position);
	}
}
