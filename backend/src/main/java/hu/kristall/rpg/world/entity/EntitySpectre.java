package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.item.ItemGenerator;

public class EntitySpectre extends CombatEntity {
	
	private ItemGenerator dungeonKeyGenerator;
	
	public EntitySpectre(World world, int entityID, Position startPosition) {
		super(world, entityID,startPosition, 1, 5, 5, 50, 50, 10, 1, 3, EntityType.SPECTRE);
		
		dungeonKeyGenerator = getWorld().getItemMap().getItem("dungeon_key");
		setName("§cKulcs őrzője");
	}
	
	@Override
	public void kill() {
		getWorld().spawnItem(dungeonKeyGenerator.get(), getPosition());
		super.kill();
	}
	
}
