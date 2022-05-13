package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

public class EntitySkeleton extends AIEntity {
	
	public EntitySkeleton(World world, int entityID, Position startPosition) {
		super(world, EntityType.SKELETON, entityID, 1, 100, 100, startPosition, 1000);
		
	}
	
	@Override
	protected void updateAI() {
		Entity target = null;
		for (Entity entity : getWorld().getEntities()) {
			if(entity.type() == EntityType.HUMAN) {
				target = entity;
			}
		}
		if(target == null) {
			return;
		}
		Position targetPosition = target.getPosition();
		this.move(targetPosition);
 		if(Position.distance(targetPosition, this.getPosition()) < 1) {
			 this.attack(target, 2);
		}
	}
	
}
