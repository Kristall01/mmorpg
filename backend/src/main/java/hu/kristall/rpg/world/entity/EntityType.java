package hu.kristall.rpg.world.entity;

public enum EntityType {
	
	HUMAN(100, 2);
	
	public final double maxHP;
	public final double speed;
	
	EntityType(double maxHP, double speed) {
		this.maxHP = maxHP;
		this.speed = speed;
	}
	
}
