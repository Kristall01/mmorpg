package hu.kristall.rpg.world;

import hu.kristall.rpg.sync.ISynchronized;
import hu.kristall.rpg.Player;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.EntityType;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class WorldPlayer implements ISynchronized<WorldPlayer> {
	
	private EntityHuman entity;
	private Player player;
	private World world;
	private Synchronizer<WorldPlayer> synchronizer = new Synchronizer<>(this);
	
	public WorldPlayer(World world, Player player) {
		this.world = world;
		this.player = player;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public Future<?> runTask(Runnable task) {
		return world.runTask(task);
	}
	
	@Override
	public <T> Future<T> computeTask(Callable<T> c) {
		return world.computeTask(c);
	}
	
	@Override
	public Synchronizer<WorldPlayer> getSynchronizer() {
		return synchronizer;
	}
	
	public EntityHuman getEntity() {
		return entity;
	}
	
	public EntityHuman spawnTo(Position pos) {
		if(this.entity == null || this.entity.isRemoved()) {
			this.entity = (EntityHuman) world.spawnEntity(EntityType.HUMAN, pos);
			this.entity.setWorldPlayer(this);
		}
		return this.entity;
	}
	
}
