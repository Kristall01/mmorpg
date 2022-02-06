package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.ISynchronized;
import hu.kristall.rpg.Player;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.Synchronizer;
import hu.kristall.rpg.network.packet.out.PacketOutMoveentity;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.ConstantPosition;
import hu.kristall.rpg.world.path.Path;
import hu.kristall.rpg.world.path.PositionFunction;

import java.util.concurrent.Future;

public class EntityPlayer extends Entity implements ISynchronized<EntityPlayer> {
	
	private final Synchronizer<EntityPlayer> synchronizer;
	private PositionFunction positionFn;
	private Player player;
	
	public EntityPlayer(World world, int entityID, Player player, Position startPosition) {
		super(world, EntityType.PLAYER, entityID, 3+((Math.random()-0.5)));
		synchronizer = new Synchronizer<>(this);
		this.player = player;
		this.positionFn = new ConstantPosition(startPosition);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public Position getPosition() {
		return positionFn.getCurrentLocation();
	}
	
	@Override
	public Future<?> runTask(Runnable task) {
		return getWorld().runTask(task);
	}
	
	@Override
	public Synchronizer<EntityPlayer> getSynchronizer() {
		return synchronizer;
	}
	
	@Override
	public void move(Position to) {
		long now = System.nanoTime();
		Path p = this.getWorld().interpolatePath(getPosition(), to, getSpeed(), now);
		this.positionFn = p.getPosiFn();
		getWorld().broadcastPacket(new PacketOutMoveentity(this.getID(), p, now));
	}
	
}
