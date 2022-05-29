package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

public class EntityOgre extends CombatEntity {
	public EntityOgre(World world, int entityID, Position startPosition) {
		super(world, entityID, startPosition, 1, 2, 5, 500, 500, 0.5, 0.4, 40, EntityType.OGRE);
	}
	
}
