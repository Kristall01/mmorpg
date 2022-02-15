package hu.kristall.rpg;

import hu.kristall.rpg.command.CommandCollections;
import hu.kristall.rpg.command.CommandMap;
import hu.kristall.rpg.lang.Lang;
import hu.kristall.rpg.network.NetworkServer;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.sync.SynchronizedObject;
import hu.kristall.rpg.sync.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

public class Server extends SynchronizedObject<Server> {
	
	private NetworkServer networkServer;
	private CommandMap commandMap;
	//private InputReader inputReader;
	private Lang lang;
	private WorldsManager worldsManager;
	private Map<String, Player> players = new HashMap<>();
	private LinkedList<Consumer<Server>> shutdownListeners = new LinkedList<>();
	private boolean stopping = false;
	private Logger logger = LoggerFactory.getLogger("server");
	
	private Server() {
		super("server");
		try {
			this.networkServer = new NetworkServer(this);
			lang = new Lang();
			lang.loadConfigFromJar("lang.cfg");
			
			commandMap = CommandCollections.base(this);
			//this.inputReader = new InputReader(text -> getSynchronizer().sync(srv -> srv.getCommandMap().executeConsoleCommand(text)), this.commandMap);
			this.worldsManager = new WorldsManager(this);
			this.networkServer.startAcceptingConnections();
			this.worldsManager.createWorld("w0", 20, 20);
			this.worldsManager.createWorld("w1", 15, 15);
			this.worldsManager.createWorld("w2", 10, 10);
		}
		catch (Throwable t) {
			logger.error("failed to bootstrap server");
			t.printStackTrace();
			this.shutdown();
		}
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public void addShutdownListener(Consumer<Server> r) {
		shutdownListeners.add(r);
	}
	
	public boolean isStopping() {
		return stopping;
	}
	
	@Override
	public void shutdown() {
		if(stopping) {
			return;
		}
		this.stopping = true;
		logger.info("shutting down server");
		networkServer.stop();
		for (Consumer<Server> shutdownListener : shutdownListeners) {
			shutdownListener.accept(this);
		}
		getSynchronizer().sync(srv -> {
			this.worldsManager.shutdown();
			this.getSynchronizer().changeObject(null);
			getSynchronizer().sync(s -> super.shutdown());
		});
	}
	
	public static Synchronizer<Server> createServer() {
		Server s = new Server();
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
	
	public Player createPlayer(PlayerConnection conn, String name) throws PlayerNameAlreadyOnlineException{
		if(players.containsKey(name)) {
			throw new PlayerNameAlreadyOnlineException();
		}
		Player p = new Player(this, () -> quitPlayer(name), conn, name);
		players.put(name, p);
		conn.joinGame(p);
		p.scheduleWorldChange(getWorldsManager().getDefaultWorld());
		return p;
	}
	
	private void quitPlayer(String name) {
		Player p = players.remove(name);
		p.scheduleWorldChange(null);
	}
	
	/**
	 * This exception is thrown when there is an authenticated connection with the given username
	 */
	public static class PlayerNameAlreadyOnlineException extends Exception{}
	
}
