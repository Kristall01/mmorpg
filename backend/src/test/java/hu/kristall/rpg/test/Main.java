package hu.kristall.rpg.test;

import hu.kristall.rpg.*;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.persistence.Savefile;
import hu.kristall.rpg.sync.AsyncExecutor;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.plan.FreePathFinder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class Main {

	@Test
	public void testSavefile() {
		Savefile savefile = null;
		try (Reader inputStream = new FileReader("src/test/resources/test_savefile.json")) {
			savefile = Utils.gson().fromJson(inputStream, Savefile.class);
		}
		catch (IOException e) {
			fail(e);
		}
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", savefile, 8007);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> level0 = worldsManager.getWorld("level0");
				if(level0 == null) {
					return false;
				}
				try {
					if(!level0.syncCompute(level -> level.getWidth() == 2 && level.getHeight() == 1).get()) {
						return false;
					}
				}
				catch (Throwable e) {
					e.printStackTrace();
					return false;
				}
				
				Synchronizer<World> level1 = worldsManager.getWorld("level1");
				if(level1 == null) {
					return false;
				}
				try {
					if(!level1.syncCompute(level -> level.getWidth() == 2 && level.getHeight() == 2).get()) {
						return false;
					}
				}
				catch (Throwable e) {
					e.printStackTrace();
					return false;
				}
				return true;
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testWorldCreation() {
		try {
			Synchronizer<Server> asyncServer = Server.createServer("/", null, 8008);
			Future<Boolean> f = asyncServer.syncCompute(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				Synchronizer<World> asyncWorld = worldsManager.createWorld("asd",2,2,new String[]{"A","B","C","D"}, new FreePathFinder());
				try {
					asyncWorld.syncCompute(world -> world.getWidth() == 1 && world.getHeight() == 1);
				}
				catch (Synchronizer.TaskRejectedException e) {
					return false;
				}
				return worldsManager.getWorld("asd") != null;
			});
			assertTrue(f.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			fail(e);
		}
	}
	
	@Test
	public void testPlayerWorldChange() {
		try {
			CompletableFuture<Boolean> worldChangeFutureResult = new CompletableFuture<>();
			final Synchronizer<Server> asyncServer = Server.createServer("/", null, 8009);
			asyncServer.sync(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				worldsManager.createWorld("w0",2,2,new String[]{"A","B","C","D"}, new FreePathFinder());
				final Synchronizer<World> asyncW1 = worldsManager.createWorld("w1",2,2,new String[]{"A","B","C","D"}, new FreePathFinder());
				try {
					Future<Player> playerFuture = srv.createPlayer(new PlayerConnection() {
						private Player player;
						public Player getPlayer() {return player;}
						public void close(String reason) {}
						public void sendPacket(PacketOut packet) {}
						public void joinGame(Player player) {
							this.player = player;
						}
					},"teszt");
					AsyncExecutor.instance().runTask(() -> {
						try {
							final Player player = playerFuture.get();
							asyncServer.sync((srv_1) -> {
								Future<?> worldChangeFuture = player.scheduleWorldChange(new WorldPosition(asyncW1, null));
								AsyncExecutor.instance().runTask(() -> {
									try {
										worldChangeFuture.get();
										player.getAsyncEntity().sync(asyncPlayerEntity -> {
											if(asyncPlayerEntity.getWorld().getSynchronizer().equals(asyncW1)) {
												worldChangeFutureResult.complete(true);
											}
											else {
												worldChangeFutureResult.complete(false);
											}
										});
									}
									catch (Throwable e) {
										worldChangeFutureResult.completeExceptionally(e);
									}
								});
							});
						}
						catch (Throwable e) {
							worldChangeFutureResult.completeExceptionally(e);
							throw new RuntimeException(e);
						}
					});
				}
				catch (Server.PlayerNameAlreadyOnlineException e) {
					worldChangeFutureResult.completeExceptionally(e);
				}
			});
			assertTrue(worldChangeFutureResult.get());
			asyncServer.sync(Server::shutdown);
		}
		catch (Throwable e) {
			
			fail(e);
		}
	}

}
