package hu.kristall.rpg.world.entity.cozy;

public enum Cloth {
	
	BASIC(1,0,0, false),
	CLOWN(1,1,1, false),
	CLOWN_BLUE(1,1,1, false),
	DRESS_WITCH(1,1,1, false),
	FLORAL(1,0,0, false),
	OVERALL(1,0,0, false),
	PANTS(0,1,0, false),
	PANTS_SUIT(0,1,0, false),
	PUMPKIN(1,1,1, false),
	PUMPKIN_PURPLE(1,1,1, false),
	SAILOR_BOW(1,0,0, false),
	SHOES(0,0,1, false),
	SKIRT(0,1,0, false),
	SPOOKY(1,1,1, false),
	SPORTY(1,0,0, false),
	SUIT(1,0,0, false),
	
	NO_TOP(1,0,0, true),
	NO_BOTTOM(0,1,0, true),
	NO_SHOES(0,0,1, true);

	public final byte bitmap;
	public final boolean transparent;
	
	Cloth(int top, int bottom, int boots, boolean transparent) {
		this.bitmap = (byte) (((top&0x1) << 2) | ((bottom&0x1) << 1) | (boots & 0x1));
		this.transparent = transparent;
	}
	
	public boolean isTop() {
		return (bitmap & 4) == 4;
	}
	
	public boolean isBottom() {
		return (bitmap & 2) == 2;
	}
	
	public boolean isShoes() {
		return (bitmap & 1) == 1;
	}
	
}
