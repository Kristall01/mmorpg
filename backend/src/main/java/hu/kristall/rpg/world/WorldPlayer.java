package hu.kristall.rpg.world;

import hu.kristall.rpg.AsyncPlayer;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.ISynchronized;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.EntityType;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class WorldPlayer implements ISynchronized<WorldPlayer> {
	
	private EntityHuman entity;
	private AsyncPlayer player;
	private World world;
	private Synchronizer<WorldPlayer> synchronizer = new Synchronizer<>(this);
	
	public WorldPlayer(World world, AsyncPlayer player) {
		this.world = world;
		this.player = player;
	}
	
	public World getWorld() {
		return world;
	}
	
	public AsyncPlayer getAsyncPlayer() {
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
	public boolean isShutdown() {
		return world.isShutdown();
	}
	
	@Override
	public Synchronizer<WorldPlayer> getSynchronizer() {
		return synchronizer;
	}
	
	public EntityHuman getEntity() {
		return entity.isRemoved() ? null : entity;
	}
	
	public EntityHuman spawnTo(Position pos) {
		if(this.entity == null || this.entity.isRemoved()) {
			this.entity = (EntityHuman) world.spawnEntity(EntityType.HUMAN, pos);
			this.entity.setWorldPlayer(this);
			this.entity.setName(player.name);
		}
		return this.entity;
	}
	
	public boolean hasEntity() {
		return !(entity == null || entity.isRemoved());
	}
}
