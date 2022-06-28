package hu.kristall.rpg.world.entity.ai;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Cancelable;
import hu.kristall.rpg.world.entity.CombatEntity;
import hu.kristall.rpg.world.entity.Entity;

public class AggressiveAttackmove implements AiTask {
	
	private Cancelable aiTask, autoAttackTask;
	private Entity target;
	private CombatEntity actor;
	private long lastAttack;
	
	public AggressiveAttackmove (CombatEntity actor, Entity target, long lastAttack) {
		this.actor = actor;
		this.target = target;
		this.lastAttack = lastAttack;
		
		this.aiTask = actor.getWorld().getTimer().schedule(this::tick, 0, 100);
	}
	
	@Override
	public boolean cancel() {
		aiTask.cancel();
		if(autoAttackTask != null) {
			autoAttackTask.cancel();
		}
		return true;
	}
	
	private void autoAttackTick(Cancelable c) {
		if(target.isRemoved() || Position.distance(actor.getPosition(), target.getPosition()) > actor.getAttackRange()) {
			return;
		}
		lastAttack = System.currentTimeMillis();
		actor.attack(target, actor.getAttackDamage());
	}
	
	private void tick(Cancelable c) {
		if(target.isRemoved() || Position.distance(actor.getPosition(), target.getPosition()) > actor.getTargetFollowDistance()) {
			aiTask.cancel();
			if(autoAttackTask != null) {
				autoAttackTask.cancel();
			}
			actor.changeAI(new PassiveWander(actor, lastAttack));
			return;
		}
		if(Position.distance(actor.getPosition(), target.getPosition()) > actor.getAttackRange()) {
			if(autoAttackTask != null) {
				autoAttackTask.cancel();
				autoAttackTask = null;
			}
			else {
				actor.move(target.getPosition());
			}
		}
		else {
			if(actor.getLastPath().getPosiFn().moving()) {
				actor.stop();
			}
			if(autoAttackTask == null) {
				long attackDelay = actor.getAttackDelay();
				autoAttackTask = actor.getWorld().getTimer().schedule(this::autoAttackTick, Math.max(0, attackDelay - (System.currentTimeMillis() - lastAttack)), attackDelay);
			}
		}
/*		m.move(target.getPosition());
		Position myPosition = getPosition();
		for (Entity entity : getWorld().getEntities()) {
			if(!(entity.isRemoved() || entity.type() != EntityType.HUMAN || Position.distance(myPosition, entity.getPosition()) > targetLockDistance)) {
				cancelable.cancel();
				//start combat
				return;
			}
		}*/
	}
	
}
