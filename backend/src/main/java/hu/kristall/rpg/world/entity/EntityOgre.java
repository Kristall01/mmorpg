package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOutAttack;
import hu.kristall.rpg.world.World;

public class EntityOgre extends AIEntity {
	public EntityOgre(World world, int entityID, Position startPosition) {
		super(world, EntityType.OGRE, entityID, 2, 100, 100, startPosition, 1000);
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
