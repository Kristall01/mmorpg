package hu.kristall.rpg.world;

import hu.kristall.rpg.sync.Synchronizer;

public class AsyncWorld extends Synchronizer<World> {
	
	public final String name;
	
	public AsyncWorld(World world, String name) {
		super(world);
		this.name = name;
	}
	
}
