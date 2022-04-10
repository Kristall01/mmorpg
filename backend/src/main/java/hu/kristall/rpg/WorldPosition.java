package hu.kristall.rpg;

import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;

public class WorldPosition {
	
	public final Synchronizer<World> world;
	public final Position pos;
	
	public WorldPosition(Synchronizer<World> world, Position pos) {
		this.world = world;
		this.pos = pos;
	}
	
}
