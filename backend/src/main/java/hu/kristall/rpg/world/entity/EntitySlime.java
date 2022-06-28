package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.entity.ai.Follow;
import hu.kristall.rpg.world.entity.ai.AiEntity;

public class EntitySlime extends AiEntity {
	
	private Entity followedEntity;
	

	public static EntitySlime createSlime(World world, int entityID, Position spawnPosition, Object followedEntity) {
		if(!(followedEntity instanceof Entity)) {
			world.getLogger().error("failed to create entity pet (no base entity was given/not instance of Entity)");
			return null;
		}
		return new EntitySlime(world, entityID, spawnPosition, (Entity) followedEntity);
	}
	
	
	private EntitySlime(World world, int entityID, Position spawnPosition, Entity followed) {
		super(world, EntityType.SLIME, entityID, followed.getSpeed()*1.2, 1, 1, spawnPosition);
		
		this.followedEntity = followed;
		String followedName = followed.getName();
		if(followedName != null) {
			this.setName(followedName+"'s pet");
		}
		
		changeAI(new Follow(this,followedEntity));
	}
	
	@Override
	public double damage(double amount, Entity source) {
		return 0;
	}
	
}
