package hu.kristall.rpg.test;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.WorldPosition;
import hu.kristall.rpg.WorldsManager;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.persistence.Savefile;
import hu.kristall.rpg.sync.AsyncExecutor;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.plan.FreePathFinder;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.Socket;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class Main {
	
	@Test
	public void testSavefile() {
		Savefile savefile = null;
		try (Reader inputStream = new FileReader("src/test/resources/test_savefile.json")) {
			savefile = hu.kristall.rpg.Utils.gson().fromJson(inputStream, Savefile.class);
		}
		catch (IOException e) {
			fail(e);
		}
		try {
			Synchronizer<Server> asyncServer = Server.createServer(savefile, 8007, null);
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
	public void testServerPortCapture() {
		try {
			final Synchronizer<Server> asyncServer = TestUtils.createTestServer(null);
			int port = asyncServer.syncCompute(srv -> srv.port).get();
			Throwable failedThrowable = null;
			try {
				Socket s = new Socket("localhost", port);
				s.close();
			}
			catch (Throwable t) {
				failedThrowable = t;
			}
			finally {
				asyncServer.sync(Server::shutdown);
			}
			if(failedThrowable != null) {
				fail(failedThrowable);
			}
			
		}
		catch (Throwable t) {
			fail(t);
		}
	}
	
	@Test
	public void testPlayerWorldChange() {
		try {
			CompletableFuture<Boolean> worldChangeFutureResult = new CompletableFuture<>();
			final Synchronizer<Server> asyncServer = TestUtils.createTestServer(null);
			asyncServer.sync(srv -> {
				WorldsManager worldsManager = srv.getWorldsManager();
				worldsManager.createWorld("w0",2,2,new String[]{"A","B","C","D"}, new FreePathFinder(), Collections.emptyList());
				final Synchronizer<World> asyncW1 = worldsManager.createWorld("w1",2,2,new String[]{"A","B","C","D"}, new FreePathFinder(), Collections.emptyList());
				try {
					Future<Player> playerFuture = srv.createPlayer(new PlayerConnection() {
						private Player player;
						public Player getPlayer() {return player;}
						public void close(String reason) {
							try {
								asyncServer.sync(srv -> {
									player.handleQuit();
								});
							}
							catch (Synchronizer.TaskRejectedException e) {
								throw new RuntimeException(e);
							}
						}
						public void sendPacket(PacketOut packet) {}
						public void joinGame(Player player) {
							this.player = player;
						}
					},"teszt", false);
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
						}
					});
				}
				catch (Server.JoinDeniedException e) {
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
