package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOutAttack;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.item.ItemGenerator;

public class EntitySkeleton extends AIEntity {
	
	private ItemGenerator boneGenerator;
	
	public EntitySkeleton(World world, int entityID, Position startPosition) {
		super(world, EntityType.SKELETON, entityID, 2.5, 100, 100, startPosition, 1000);
		boneGenerator = world.getItemMap().getItem("skeleton_bone");
	}
	
	@Override
	public void kill() {
		Position pos = getPosition();
		getWorld().spawnItem(boneGenerator.get(), getWorld().getRandomPositionNear(pos, 0.5, 1));
		super.kill();
	}
	
	@Override
	protected void updateAI() {
		Entity target = null;
		for (Entity entity : getWorld().getEntities()) {
			if(entity.type() == EntityType.HUMAN) {
				target = entity;
			}
		}
		if(target == null) {
			return;
		}
		Position targetPosition = target.getPosition();
		this.move(targetPosition);
 		if(Position.distance(targetPosition, this.getPosition()) < 1) {
			 this.attack(target, 2);
		}
	}
	
	@Override
	public double attack(Entity entity, double damage) {
		double d = super.attack(entity, damage);
		getWorld().broadcastPacket(new PacketOutAttack(this, entity.getPosition()));
		return d;
	}
}
