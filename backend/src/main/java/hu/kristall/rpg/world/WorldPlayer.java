package hu.kristall.rpg.world;

import hu.kristall.rpg.AsyncPlayer;
import hu.kristall.rpg.Position;
import hu.kristall.rpg.WorldPosition;
import hu.kristall.rpg.persistence.SavedPlayer;
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
	private boolean changingWorld;
	
	private boolean hasQuit = false;
	
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
	
	public EntityHuman spawnTo(Position pos, SavedPlayer savedPlayer) {
		if(this.entity == null || this.entity.isRemoved()) {
			EntityHuman h = (EntityHuman) world.spawnEntity(EntityType.HUMAN, pos, savedPlayer);
			h.setWorldPlayer(this);
			h.setName(player.name);
			this.entity = h;
		}
		return this.entity;
	}
	
	private void stopChangingWorld() {
		changingWorld = false;
	}
	
	public void startChangingWorld(final String targetWorld) {
		if(this.changingWorld) {
			return;
		}
		this.changingWorld = true;
		try {
			player.sync(syncedPLayer -> {
				Synchronizer<World> w = syncedPLayer.getServer().getWorldsManager().getWorld(targetWorld);
				if(w == null) {
					try {
						syncedPLayer.getAsyncEntity().sync(me -> {
							if(me == null) {
								return;
								//player already left this world. no need to do anything
							}
							me.stopChangingWorld();
						});
					}
					catch (Synchronizer.TaskRejectedException e) {
						//world wont be shut down while server is running
						e.printStackTrace();
					}
				}
				else {
					syncedPLayer.scheduleWorldChange(new WorldPosition(w,null));
				}
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//world won't be shutting down here
			e.printStackTrace();
		}
	}
	
	public boolean hasEntity() {
		return !(entity == null || entity.isRemoved());
	}
	
	public void quit() {
		this.hasQuit = true;
	}
	
	public boolean hasQuit() {
		return this.hasQuit;
	}
	
}
