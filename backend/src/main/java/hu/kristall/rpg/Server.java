package hu.kristall.rpg;

import hu.kristall.rpg.command.CommandCollections;
import hu.kristall.rpg.command.CommandMap;
import hu.kristall.rpg.lang.Lang;
import hu.kristall.rpg.network.NetworkServer;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.persistence.*;
import hu.kristall.rpg.sync.AsyncExecutor;
import hu.kristall.rpg.sync.SynchronizedObject;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.Portal;
import hu.kristall.rpg.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Server extends SynchronizedObject<Server> {
	
	private NetworkServer networkServer;
	private CommandMap commandMap;
	//private InputReader inputReader;
	private Lang lang = new Lang();
	private WorldsManager worldsManager;
	private Map<String, Player> players = new HashMap<>();
	private Set<String> takeNames = new HashSet<>();
	private LinkedList<Consumer<Server>> shutdownListeners = new LinkedList<>();
	private boolean stopping = false;
	private final Object stoppingLock = new Object();
	private Logger logger = LoggerFactory.getLogger("server");
	private PlayerPersistence playerPersistence;
	
	private Server(String servePath, Savefile savefile) throws IOException {
		super("server");
		
		
		changeSyncer(new AsyncServer(this));
		this.playerPersistence = new PlayerPersistence(new File(System.getProperty("user.dir"), "playerdata"));
		try {
			lang.loadConfigFromJar("lang.cfg");
			this.networkServer = new NetworkServer(this, servePath);

			
			commandMap = CommandCollections.base(this);
			//this.inputReader = new InputReader(text -> getSynchronizer().sync(srv -> srv.getCommandMap().executeConsoleCommand(text)), this.commandMap);
			this.worldsManager = new WorldsManager(this);
			this.networkServer.startAcceptingConnections();
			
			
			for (Map.Entry<String, SavedLevel> levelEntry : savefile.levels.entrySet()) {
				SavedLevel level = levelEntry.getValue();
				Synchronizer<World> asyncWorld = worldsManager.createWorld(levelEntry.getKey(), level.width, level.height, level.layers, level.pathFinder);
				final List<SavedPortal> portals = level.portals;
				asyncWorld.sync(world -> {
					for (SavedPortal portal : portals) {
						world.addPortal(new Portal(portal.position, portal.targetWorld, portal.targetPosition));
					}
				});
			}
			/*if(savefile != null) {
				for (Map.Entry<String, SavedLevel> levelEntry : savefile.levels.entrySet()) {
					SavedLevel level = levelEntry.getValue();
					this.worldsManager.createWorld(levelEntry.getKey(), level.width, level.height).sync(w -> {
						for (SavedPortal portal : level.portals) {
							w.addPortal(new Portal(portal.position, portal.targetWorld));
						}
					});
				}
			}*/
		}
		catch (Throwable t) {
			logger.error(lang.getMessage("bootstrap.failed"), t);
			this.shutdown();
		}
	}
	
	@Override
	public AsyncServer getSynchronizer() {
		return (AsyncServer) super.getSynchronizer();
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public Player getPlayer(String name) {
		return players.get(name);
	}
	
	public void addShutdownListener(Consumer<Server> r) {
		shutdownListeners.add(r);
	}
	
	public boolean isStopping() {
		//synchronized block is required, because this method can be called from any thread
		synchronized(stoppingLock) {
			return stopping;
		}
	}
	
	@Override
	public void shutdown() {
		//synchronized block is required, because this method can be called from any thread
		synchronized(stoppingLock) {
			if(stopping) {
				return;
			}
			this.stopping = true;
		}
		for (Consumer<Server> shutdownListener : shutdownListeners) {
			shutdownListener.accept(this);
		}
		logger.info(lang.getMessage("server.shutting"));
		networkServer.stop(() -> {
			try {
				getSynchronizer().sync(srv -> {
					this.worldsManager.shutdown();
					try {
						getSynchronizer().sync(s -> super.shutdown());
					}
					catch (Synchronizer.TaskRejectedException e) {
						//server wont be shut down until super.shutdown() is called
					}
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//server wont be shut down until super.shutdown() is called
				e.printStackTrace();
			}
		});
	}
	
	public static Synchronizer<Server> createServer(String servePath, Savefile save) throws IOException {
		Server s = new Server(servePath, save);
		return s.getSynchronizer();
	}
	
	//------------ GETTERS //------------
	
	public Lang getLang() {
		return lang;
	}
	
	public CommandMap getCommandMap() {
		return commandMap;
	}
	
	public NetworkServer getNetworkServer() {
		return networkServer;
	}
	
	public WorldsManager getWorldsManager() {
		return worldsManager;
	}
	
	public Future<Player> createPlayer(PlayerConnection conn, String name) throws PlayerNameAlreadyOnlineException{
		if(takeNames.contains(name)) {
			throw new PlayerNameAlreadyOnlineException();
		}
		takeNames.add(name);
		CompletableFuture<Player> c = new CompletableFuture<>();
		final Synchronizer<Server> serverSynchronizer = this.getSynchronizer();
		AsyncExecutor.instance().runTask(() -> {
			SavedPlayer savedPlayer = null;
			try {
				savedPlayer = playerPersistence.loadPlayer(name);
			}
			catch (Throwable e) {
				AsyncExecutor.instance().getLogger().error(lang.getMessage("server.playerdata.loadfail"), e);
				c.completeExceptionally(e);
				return;
			}
			final SavedPlayer finalSavedPlayer = savedPlayer;
			try {
				serverSynchronizer.sync(srv -> {
					Player p = new Player(this, finalSavedPlayer, this.playerPersistence, () -> quitPlayer(name), conn, name);
					players.put(name, p);
					c.complete(p);
					conn.joinGame(p);
					Synchronizer<World> targetWorld = null;
					Position targetPos = null;
					if(finalSavedPlayer != null) {
						LogoutPosition lp = finalSavedPlayer.logoutPosition;
						if(lp != null) {
							if(lp.worldName != null) {
								targetWorld = getWorldsManager().getWorld(lp.worldName);
							}
							if(lp.pos != null) {
								targetPos = lp.pos;
							}
						}
					}
					if(targetWorld == null) {
						targetWorld = getWorldsManager().getDefaultWorld();
					}
					WorldPosition worldPosition = new WorldPosition(targetWorld, targetPos);
					p.scheduleWorldChange(worldPosition);
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//server was shut down during loading player. feels bad man :/
				e.printStackTrace();
				c.completeExceptionally(e);
			}
		});
		return c;
	}
	
	private void quitPlayer(String name) {
		Player p = players.remove(name);
		if(p == null) {
			return;
		}
		takeNames.remove(name);
		p.scheduleWorldChange(null);
	}
	
	/*
	public Future<Savefile> serialize() {
		Map<String, SavedLevel> levels = new HashMap<>();
		List<Future<SavedLevel>> levelFutures = new ArrayList<>();
		for (Map.Entry<String, Synchronizer<World>> world : worldsManager.getWorlds()) {
			try {
				levelFutures.add(world.getValue().syncCompute(World::serialize));
			}
			catch (Synchronizer.TaskRejectedException e) {
				//world should be running
				e.printStackTrace();
			}
		}
		CompletableFuture<Savefile> s = new CompletableFuture<>();
		AsyncExecutor.instance().runTask(() -> {
			Map<String, SavedLevel> levelMap = new HashMap<>();
			boolean error = false;
			for (Future<SavedLevel> levelFuture : levelFutures) {
				SavedLevel l = null;
				try {
					l = levelFuture.get();
					levelMap.put(l.name, l);
				}
				catch (Throwable e) {
					error = true;
				}
			}
			if(error) {
				s.complete(null);
			}
			else {
				s.complete(new Savefile(levelMap, new HashMap<>(), "default"));
			}
		});
		return s;
	}
	*/
	
	/**
	 * This exception is thrown when there is an authenticated connection with the given username
	 */
	public static class PlayerNameAlreadyOnlineException extends Exception{}
	
}
