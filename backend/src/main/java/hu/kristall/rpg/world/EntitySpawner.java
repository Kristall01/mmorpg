package hu.kristall.rpg.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityType;

public class EntitySpawner {
	
	private int count;
	private EntityType type;
	private Position topLeft;
	private long respawnInterval;
	private double horizontalDiff, verticalDiff;
	
	public EntitySpawner(int count, long respawnInterval, EntityType type, Position topLeft, Position bottomRight) {
		this.count = count;
		this.type = type;
		this.topLeft = topLeft;
		this.respawnInterval = respawnInterval;
		
		this.horizontalDiff = Math.max(0, bottomRight.getX() - topLeft.getX());
		this.verticalDiff = Math.max(0, bottomRight.getY() - topLeft.getY());
	}
	
	private void doRespawning(World world) {
		int sum = 0;
		for (Entity entity : world.getEntities()) {
			if(entity.type().equals(this.type)) {
				++sum;
			}
		}
		for (int i = sum; i < count; ++i) {
			double randX = (topLeft.getX() + Utils.random.nextDouble() * horizontalDiff);
			double randY = (topLeft.getY() + Utils.random.nextDouble() * verticalDiff);
			world.spawnEntity(type, new Position(randX, randY));
		}
	}
	
	public void registerTo(World world) {
		world.getTimer().schedule((c) -> doRespawning(world), 0, respawnInterval);
	}
	
}
