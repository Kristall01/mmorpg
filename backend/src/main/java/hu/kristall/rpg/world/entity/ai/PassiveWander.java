package hu.kristall.rpg.world.entity.ai;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Cancelable;
import hu.kristall.rpg.world.entity.CombatEntity;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.EntityType;

public class PassiveWander implements AiTask {
	
	private CombatEntity actor;
	private long idleUntil = 0;
	private boolean idleStarted = false;
	private long lastAttack;
	
	Cancelable task;
	
	public PassiveWander(CombatEntity actor, long lastAttack) {
		this.actor = actor;
		
		this.task = actor.getWorld().getTimer().schedule(this::tick, 0, 500);
		this.lastAttack = lastAttack;
	}
	
	private void tick(Cancelable c) {
		Position myPosition = actor.getPosition();
		for (Entity entity : actor.getWorld().getEntities()) {
			if(!entity.isRemoved() && entity.type() == EntityType.HUMAN && !((EntityHuman)entity).isNPC() && Position.distance(myPosition, entity.getPosition()) < actor.getTargetLockDistance()) {
				task.cancel();
				actor.changeAI(new AggressiveAttackmove(actor, entity, lastAttack));
				//start combat
				return;
			}
		}
		if(!actor.getLastPath().getPosiFn().moving()) {
			if(System.nanoTime() > idleUntil) {
				if(!idleStarted) {
					idleUntil = System.nanoTime() + (long)(2 + Math.random())*1000000000L;
					idleStarted = true;
				} else {
					actor.move(actor.getWorld().getRandomPositionNear(actor.getPosition(), 3, 6));
				}
			}
		}
		else {
			idleStarted = false;
		}
	}
	
	@Override
	public boolean cancel() {
		return task.cancel();
	}
	
}
