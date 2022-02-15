package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.packet.out.PacketOutMoveentity;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.WorldPlayer;
import hu.kristall.rpg.world.path.ConstantPosition;
import hu.kristall.rpg.world.path.Path;

import java.util.List;

public class EntityHuman extends Entity {
	
	private WorldPlayer worldPlayer;
	private Path lastPath;
	
	public EntityHuman(World world, int entityID, Position startPosition) {
		super(world,EntityType.HUMAN,  entityID, 2);
		this.lastPath = new Path(startPosition, List.of(startPosition, startPosition), new ConstantPosition(startPosition), System.nanoTime());
	}
	
	public WorldPlayer getWorldPlayer() {
		return worldPlayer;
	}
	
	public void setWorldPlayer(WorldPlayer worldPlayer) {
		this.worldPlayer = worldPlayer;
	}
	
	@Override
	public Position getPosition() {
		return lastPath.getPosiFn().getCurrentLocation();
	}
	
	@Override
	public Path getLastPath() {
		return lastPath;
	}
	
	@Override
	public void move(Position to) {
		long now = System.nanoTime();
		Path p = this.getWorld().interpolatePath(getPosition(), to, getSpeed(), now);
		this.lastPath = p;
		getWorld().broadcastPacket(new PacketOutMoveentity(this));
	}
	
}
