package hu.kristall.rpg.world;

public class PotionEffect {
	
	private PotionEffectType type;
	private int level;
	private long lastsUntil, effectTime;
	
	public PotionEffect(PotionEffectType type, int level, long effectTime) {
		this.type = type;
		this.level = level;
		this.effectTime = effectTime;
	}
	
	public PotionEffect(PotionEffectType type, int level, long lastsUntil, long effectTime) {
		this.type = type;
		this.level = level;
		this.lastsUntil = lastsUntil;
		this.effectTime = effectTime;
	}
	
	public PotionEffectType getType() {
		return type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isActive() {
		return lastsUntil > System.currentTimeMillis();
	}
	
	public void extendTime() {
		lastsUntil = Math.max(System.currentTimeMillis(), lastsUntil)+effectTime;
	}
	
	public void fixTime() {
		lastsUntil += System.currentTimeMillis();
	}
	
	public void reduceTime() {
		lastsUntil -= System.currentTimeMillis();
	}
	
	public long getRemainingTime() {
		return Math.max(0, (lastsUntil - System.currentTimeMillis()));
	}
	
	public PotionEffect structuredClone() {
		return new PotionEffect(type, level, lastsUntil, effectTime);
	}
	
}
