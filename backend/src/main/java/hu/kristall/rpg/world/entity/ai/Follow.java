package hu.kristall.rpg.world.entity.ai;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Cancelable;
import hu.kristall.rpg.world.entity.Entity;

public class Follow implements AiTask {
	
	private Entity actor, followedEntity;
	
	public Follow(Entity actor, Entity followedEntity) {
		this.actor = actor;
		this.followedEntity = followedEntity;
		
		
		actor.getWorld().getTimer().schedule(this::tickAI,0, 1000);
	}
	
	@Override
	public boolean cancel() {
		return false;
	}
	
	private void tickAI(Cancelable c) {
		if(followedEntity.isRemoved()) {
			this.actor.remove();
			return;
		}
		Position followedEntityPosition = followedEntity.getPosition();
		if(Position.distance(actor.getLastPath().getTarget(), followedEntityPosition) > 1.5) {
			actor.move(actor.getWorld().getRandomPositionNear(followedEntityPosition, 1,1.5));
		}
	}
	
	
}
	
