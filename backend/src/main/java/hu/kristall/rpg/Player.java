package hu.kristall.rpg;

import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.sync.AsyncExecutor;
import hu.kristall.rpg.sync.ISynchronized;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.WorldPlayer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player implements PlayerSender, ISynchronized<Player> {
	
	private String name;
	private final PlayerConnection connection;
	private final Lock worldLock;
	private Synchronizer<WorldPlayer> asyncEntity = new Synchronizer<>(null, AsyncExecutor.instance());
	private final Queue<Synchronizer<World>> worldChangeQueue = new LinkedList<>();
	private final Server server;
	private Runnable quitHandler;
	private final AsyncPlayer asyncPlayer;
	
	public Player(Server server, Runnable quitHandler, PlayerConnection connection, String name) {
		this.server = server;
		this.name = name;
		this.connection = connection;
		this.quitHandler = quitHandler;
		this.worldLock = new ReentrantLock();
		this.asyncPlayer = new AsyncPlayer(this);
	}
	
	public void handleQuit() {
		this.quitHandler.run();
	}
	
	public String getName() {
		return name;
	}
	
	public PlayerConnection getConnection() {
		return connection;
	}
	
	private void repollWorldChange() {
		synchronized(worldChangeQueue) {
			Synchronizer<World> scheduledChange = worldChangeQueue.poll();
			if(scheduledChange != null) {
				exclusiveWorldChange(scheduledChange);
			}
			else {
				worldLock.unlock();
			}
		}
	}
	
	private void exclusiveWorldChange(Synchronizer<World> newAsyncWorld) {
		final Player leaver = this;
		final Synchronizer<Server> asyncServer = getServer().getSynchronizer();
		try {
			asyncEntity.sync(e -> {
				if(e != null) {
					e.getWorld().leavePlayer(leaver.asyncPlayer);
				}
				if(newAsyncWorld == null) {
					try {
						asyncServer.sync(srv -> {
							repollWorldChange();
						});
					}
					catch (Synchronizer.TaskRejectedException ex) {
						//server cannot be shut down here
						ex.printStackTrace();
						worldLock.unlock();
					}
				}
				else {
					try {
						newAsyncWorld.sync(newWorld -> {
							leaver.setAsyncEntity(newWorld.joinPlayer(this.asyncPlayer));
							try {
								asyncServer.sync(srv -> {
									repollWorldChange();
								});
							}
							catch (Synchronizer.TaskRejectedException ex) {
								//server cannot be shut down here
								ex.printStackTrace();
								worldLock.unlock();
							}
						});
					}
					catch (Synchronizer.TaskRejectedException ex) {
						//worlds cannot be shut down during server running
						worldLock.unlock();
						ex.printStackTrace();
					}
				}
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//worlds cannot be shut down during server running
			e.printStackTrace();
		}
	}
	
	public void scheduleWorldChange(Synchronizer<World> newAsyncWorld) {
		synchronized(worldChangeQueue) {
			if(!worldLock.tryLock()) {
				worldChangeQueue.offer(newAsyncWorld);
				return;
			}
		}
		exclusiveWorldChange(newAsyncWorld);
	}
	
	private synchronized void setAsyncEntity(Synchronizer<WorldPlayer> asyncEntity) {
		this.asyncEntity = asyncEntity;
	}
	
	public synchronized Synchronizer<WorldPlayer> getAsyncEntity() {
		return asyncEntity;
	}
	
	// ---------------------- command methods ------------------------
	
	@Override
	public boolean hasPermission(String permission) {
		return false;
	}
	
	public void sendMessage(String message) {
		connection.sendPacket(new PacketOutChat(message));
	}
	
	public Server getServer() {
		return server;
	}
	
	// ---------------------- packet methods -------------------------
	
	/*public void spawnEntity(Entity entity) {
		connection.sendPacket(new PacketOutSpawnEntity(entity));
		connection.sendPacket(new PacketOutMoveentity(entity));
	}
	
	/*
	public void followEntity(int entityID) {
		connection.sendPacket(new PacketOutFollowEntity(entityID));
	}
	*/
	
	/*public void joinWorld(World world, Position spawnPosition) {
		connection.sendPacket(new PacketOutJoinworld(world, spawnPosition));
	}
	
	public void leaveWorld() {
	
	}*/
	
	public void kick(String reason) {
		connection.close(reason);
		this.asyncPlayer.changeObject(null);
	}
	
	@Override
	public Synchronizer<Player> getSynchronizer() {
		return this.asyncPlayer;
	}
	
	@Override
	public Future<?> runTask(Runnable task) {
		return server.runTask(task);
	}
	
	@Override
	public <T> Future<T> computeTask(Callable<T> c) {
		return server.computeTask(c);
	}
	
	@Override
	public boolean isShutdown() {
		return server.isShutdown();
	}
}
