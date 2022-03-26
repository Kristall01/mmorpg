package hu.kristall.rpg.world;

import hu.kristall.rpg.GeneratedID;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.AutoRemove;

public class FloatingItem extends AutoRemove {
	
	private World world;
	private Position position;
	private Item item;
	private GeneratedID<FloatingItem> id;
	
	public FloatingItem(World world, GeneratedID<FloatingItem> id, Position position, Item item) {
		super(world.getTimer(), 300000);
		this.id = id;
		this.world = world;
		this.position = position;
		this.item = item;
	}
	
	public GeneratedID<FloatingItem> getID() {
		return id;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Item getItem() {
		return item;
	}
	
	public GeneratedID<FloatingItem> getId() {
		return id;
	}
	
	protected void remove0() {
		world.cleanRemovedItem(this);
	}
	
}
