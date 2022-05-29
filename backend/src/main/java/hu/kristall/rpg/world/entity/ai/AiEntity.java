package hu.kristall.rpg.world.entity.ai;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.entity.EntityType;
import hu.kristall.rpg.world.entity.RegularMovingEntity;

public class AiEntity extends RegularMovingEntity {
	
	private AiTask task;
	
	public AiEntity(World world, EntityType type, int entityID, double speed, double HP, double maxHp, Position startPosition) {
		super(world, type, entityID, speed, HP, maxHp, startPosition);
	}
	
	public void changeAI(AiTask task) {
		cancelTask();
		this.task = task;
	}
	
	private void cancelTask() {
		if(this.task != null) {
			this.task.cancel();
		}
	}
	
	@Override
	public void kill() {
		cancelTask();
		super.kill();
	}
}
