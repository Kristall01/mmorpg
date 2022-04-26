package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Cancelable;
import hu.kristall.rpg.world.World;

public abstract class AIEntity extends RegularMovingEntity {
	
	private Cancelable AIUpdateTask;
	
	public AIEntity(World world, EntityType type, int entityID, double speed, double HP, double maxHp, Position startPosition, long AIUpdateInterval) {
		super(world, type, entityID, speed, HP, maxHp, startPosition);
		
		AIUpdateTask = getWorld().getTimer().schedule(this::updateAI, 0, AIUpdateInterval);
	}
	
	@Override
	public void remove() {
		AIUpdateTask.cancel();
		super.remove();
	}
	
	protected abstract void updateAI();
	
}
