package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.item.ItemGenerator;

public class EntitySkeleton extends CombatEntity {
	
	private ItemGenerator boneGenerator;
	
	public EntitySkeleton(World world, int entityID, Position startPosition) {
		super(world, entityID, startPosition, 1, 2, 6,100,100, 2.5, 2.5, 3, EntityType.SKELETON);
		boneGenerator = world.getItemMap().getItem("skeleton_bone");
	}
	
	@Override
	public void kill() {
		Position pos = getPosition();
		getWorld().spawnItem(boneGenerator.get(), getWorld().getRandomPositionNear(pos, 0.5, 1));
		super.kill();
	}
	
}
