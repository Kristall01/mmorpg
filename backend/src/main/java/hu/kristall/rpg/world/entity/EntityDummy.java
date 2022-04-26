package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

public class EntityDummy extends RegularMovingEntity {
	
	public EntityDummy(World world, int entityID, Position pos) {
		super(world, EntityType.DUMMY, entityID, 1, 100, 100, pos);
	}
	
	@Override
	public void move(Position to) {
		this.teleport(to);
	}

	
}
