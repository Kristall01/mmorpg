package hu.kristall.rpg.world.item;

public class ItemFlags {
	
	public final boolean renderTitle;
	
	public ItemFlags(boolean renderTitle) {
		this.renderTitle = renderTitle;
	}
	
	public ItemFlags() {
		this(false);
	}
	
}
