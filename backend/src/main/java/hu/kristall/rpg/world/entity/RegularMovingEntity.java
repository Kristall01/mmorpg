package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOutEntityTeleport;
import hu.kristall.rpg.network.packet.out.PacketOutMoveentity;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.Path;

public abstract class RegularMovingEntity extends Entity {
	
	private Path lastPath;
	
	public RegularMovingEntity(World world, EntityType type, int entityID, double speed, double HP, double maxHp, Position startPosition) {
		super(world, type, entityID, speed, HP, maxHp);
		
		lastPath = world.idlePath(startPosition);
	}
	
	@Override
	public Position getPosition() {
		return lastPath.getPosiFn().getCurrentPosition();
	}
	
	@Override
	public Path getLastPath() {
		return lastPath;
	}
	
	public void move(Position to) {
		to = getWorld().fixValidate(to);
		long now = System.nanoTime();
		Path p = this.getWorld().findPath(getPosition(), to, getSpeed(), now);
		if(p == null) {
			return;
		}
		this.lastPath = p;
		getWorld().broadcastPacket(new PacketOutMoveentity(this));
	}
	
	@Override
	public void stop() {
		teleport(getPosition(), false);
	}
	
	public void teleport(Position pos, boolean instant) {
		this.lastPath = getWorld().idlePath(pos);
		getWorld().broadcastPacket(new PacketOutEntityTeleport(pos.getX(), pos.getY(), getID(), instant));
	}
	
	@Override
	public void teleport(Position pos) {
		teleport(pos, true);
	}
	
}
