package hu.kristall.rpg.world;

public enum LabelType {
	
	DAMAGE(0),
	HEAL(1);
	
	public final int code;
	
	LabelType(int code) {
		this.code = code;
	}
	
}
