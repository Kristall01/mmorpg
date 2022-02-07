package hu.kristall.rpg;

import hu.kristall.rpg.command.CommandCollections;
import hu.kristall.rpg.command.CommandMap;
import hu.kristall.rpg.console.CommandSubmitter;
import hu.kristall.rpg.console.InputReader;
import hu.kristall.rpg.console.PrimitiveReader;
import hu.kristall.rpg.console.TerminalReader;
import hu.kristall.rpg.lang.Lang;
import hu.kristall.rpg.network.NetworkServer;

import java.io.IOException;

public class Server extends SynchronizedObject<Server> {
	
	private NetworkServer networkServer;
	private CommandMap commandMap;
	private InputReader inputReader;
	private PlayerWorldManager playerWorldManager;
	private Lang lang;
	private WorldsManager worldsManager;
	
	private Server() {
		try {
			this.networkServer = new NetworkServer(this);
			lang = new Lang();
			lang.loadConfigFromJar("lang.cfg");
			
			commandMap = CommandCollections.base(this);
			CommandSubmitter submitter = new CommandSubmitter(getSynchronizer());
			try {
				if(System.console() == null) {
					throw new IOException("system console was not found");
				}
				this.inputReader = new InputReader(submitter, new TerminalReader(commandMap));
			}
			catch (IOException e) {
				System.out.println("no terminal was found, switching to stdin");
				this.inputReader = new InputReader(submitter, new PrimitiveReader());
			}
			
			this.playerWorldManager = new PlayerWorldManager(this);
			this.worldsManager = new WorldsManager(this);
			this.networkServer.starWsHandler();
			this.worldsManager.createWorld("default", 3, 3);
		}
		catch (Throwable t) {
			System.out.println("failed to bootstrap server");
			t.printStackTrace();
			this.shutdown();
		}
	}
	
	@Override
	protected void shutdown() {
		System.out.println("shutting down server");
		super.shutdown();
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
	
	public PlayerWorldManager getPlayerWorldManager() {
		return playerWorldManager;
	}
	
}
