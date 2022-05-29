package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOutAttack;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.entity.ai.AiEntity;
import hu.kristall.rpg.world.entity.ai.PassiveWander;

public class CombatEntity extends AiEntity {
	
	private double
		targetLockDistance,
		targetFollowDistance,
		attackDamage,
		attackRange;
		
	
	private long
		lastAttack,
		attackDelay;
	
	public CombatEntity(World world, int entityID, Position startPosition, double attackRange, double targetLockDistance, double targetFollowDistance, double HP, double maxHP, double moveSpeed, double attackSpeed, double attackDamage, EntityType type) {
		super(world, type, entityID, moveSpeed, HP, maxHP, startPosition);
		
		this.targetLockDistance = targetLockDistance;
		this.targetFollowDistance = targetFollowDistance;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.attackDelay = (long)(1000/attackSpeed);
		
		changeAI(new PassiveWander(this));
	}
	
	public double getTargetLockDistance() {
		return targetLockDistance;
	}
	
	public double getTargetFollowDistance() {
		return targetFollowDistance;
	}
	
	public double getAttackDamage() {
		return attackDamage;
	}
	
	public double getAttackRange() {
		return attackRange;
	}
	
	public long getAttackDelay() {
		return attackDelay;
	}
	
	@Override
	public double attack(Entity entity, double damage) {
		double d = super.attack(entity, damage);
		getWorld().broadcastPacket(new PacketOutAttack(this, entity.getPosition()));
		return d;
	}
	
	/*private void startAgressive(Entity target) {
		final CombatEntity m = this;
		Consumer<Cancelable> c = new Consumer<Cancelable>() {
			Cancelable autoAttackTask;
			
			@Override
			public void accept(Cancelable cancelable) {
				if(target.isRemoved() || Position.distance(m.getPosition(), target.getPosition()) > targetFollowDistance) {
					cancelable.cancel();
					if(autoAttackTask != null) {
						autoAttackTask.cancel();
					}
					startPassive();
					return;
				}
				if(Position.distance(m.getPosition(), target.getPosition()) > attackRange) {
					if(autoAttackTask != null) {
						autoAttackTask.cancel();
						autoAttackTask = null;
					}
					else {
						move(target.getPosition());
					}
				}
				else {
					if(m.getLastPath().getPosiFn().moving()) {
						m.stop();
					}
					if(autoAttackTask == null) {
						autoAttackTask = getWorld().getTimer().schedule((c) -> {
							if(target.isRemoved() || Position.distance(m.getPosition(), target.getPosition()) > attackRange) {
								return;
							}
							lastAttack = System.currentTimeMillis();
							attack(target, attackDamage);
						}, Math.max(0, attackDelay - (System.currentTimeMillis() - lastAttack)), attackDelay);
					}
				}
/*				m.move(target.getPosition());
				Position myPosition = getPosition();
				for (Entity entity : getWorld().getEntities()) {
					if(!(entity.isRemoved() || entity.type() != EntityType.HUMAN || Position.distance(myPosition, entity.getPosition()) > targetLockDistance)) {
						cancelable.cancel();
						//start combat
						return;
					}
				}*//*
			}
		};
		this.aiTask = this.getWorld().getTimer().schedule(c, 0, 100);
	}*/
	
	/*private void startPassive() {
		final CombatEntity m = this;
		Consumer<Cancelable> c = new Consumer<Cancelable>() {
			
			long idleUntil = 0;
			boolean idleStarted = false;
			
			@Override
			public void accept(Cancelable cancelable) {
				Position myPosition = getPosition();
				for (Entity entity : getWorld().getEntities()) {
					if(!(entity.isRemoved() || entity.type() != EntityType.HUMAN || Position.distance(myPosition, entity.getPosition()) > targetLockDistance)) {
						cancelable.cancel();
						startAgressive(entity);
						//start combat
						return;
					}
				}
				if(!m.getLastPath().getPosiFn().moving()) {
					if(System.nanoTime() > idleUntil) {
						if(!idleStarted) {
							idleUntil = System.nanoTime() + (long)(2 + Math.random())*1000000000L;
							idleStarted = true;
						} else {
							m.move(getWorld().getRandomPositionNear(getPosition(), 3, 6));
						}
					}
				}
				else {
					idleStarted = false;
				}
			}
		};
		this.aiTask = this.getWorld().getTimer().schedule(c, 0, 500);
	}*/
	
	
	/*
	private void lookForTarget(Cancelable c) {
		Position myPosition = getPosition();
		for (Entity entity : getWorld().getEntities()) {
			if(!(entity.isRemoved() || entity.type() != EntityType.HUMAN || Position.distance(myPosition, entity.getPosition()) > targetLockDistance)) {
				c.cancel();
				this.attack(entity);
			}
		}
	}
	
	private void cancelAttacking() {
		if(this.attackTask != null) {
			attackTask.cancel();
			attackTask = null;
		}
	}
	
	private void startAttacking(Entity enemy) {
	}
	
	private void attackMove(Position p) {
	
	}
	
	private void attack(Entity entity) {
		getWorld().getTimer().schedule((c) -> {
		
		}, 250);
	}
	*/
	
	/*@Override
	protected void updateAI() {
		boolean stopAttack = true;
		Position myPosition = getPosition();
		if(attackTarget == null || attackTarget.isRemoved() || Position.distance(myPosition, attackTarget.getPosition()) > targetFollowDistance) {
			cancelAttacking();
			attackTarget = findTarget();
		}
		if(attackTarget != null) {
			Position targetPosition = attackTarget.getPosition();
			if(Position.distance(targetPosition, myPosition) > attackRange) {
				move(getWorld().getRandomPositionNear(targetPosition, 0.5, 1.0));
			}
			else {
				if(getLastPath().getPosiFn().moving()) {
					this.stop();
				}
				startAttacking(attackTarget);
				stopAttack = false;
			}
		}
		else {
			if(!this.getLastPath().getPosiFn().moving()) {
				if(System.nanoTime() > idleUntil) {
					if(!idleStarted) {
						idleUntil = System.nanoTime() + (long)(2 + Math.random())*1000000000L;
						idleStarted = true;
					} else {
						this.move(getWorld().getRandomPositionNear(getPosition(), 3, 6));
					}
				}
			}
			else {
				idleStarted = false;
			}
		}
		if(stopAttack) {
			cancelAttacking();
		}
	}*/
	
}
