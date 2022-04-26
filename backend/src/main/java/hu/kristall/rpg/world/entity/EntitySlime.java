package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

public class EntitySlime extends AIEntity {
	
	private Entity followedEntity;
	

	public static EntitySlime createSlime(World world, int entityID, Position spawnPosition, Object followedEntity) {
		if(!(followedEntity instanceof Entity)) {
			world.getLogger().error("failed to create entity pet (no base entity was given/not instance of Entity)");
			return null;
		}
		return new EntitySlime(world, entityID, spawnPosition, (Entity) followedEntity);
	}
	
	
	private EntitySlime(World world, int entityID, Position spawnPosition, Entity followed) {
		super(world, EntityType.SLIME, entityID, followed.getSpeed()*1.2, 1, 1, spawnPosition, 500);
		
		this.followedEntity = followed;
		String followedName = followed.getName();
		if(followedName != null) {
			this.setName(followedName+"'s pet");
		}
	}
	
	@Override
	public double damage(double amount) {
		return 0;
	}
	
	@Override
	protected void updateAI() {
		if(followedEntity.isRemoved()) {
			this.remove();
			return;
		}
		Position followedEntityPosition = followedEntity.getPosition();
		if(Position.distance(getLastPath().getTarget(), followedEntityPosition) > 1.5) {
			this.move(getWorld().getRandomPositionNear(followedEntityPosition, 1,1.5));
		}
	}
	
}
