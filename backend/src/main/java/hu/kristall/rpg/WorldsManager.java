package hu.kristall.rpg;

import hu.kristall.rpg.world.World;

import java.util.HashMap;

public class WorldsManager {
	
	private final HashMap<String, Synchronizer<World>> worlds = new HashMap<>();
	private Server server;
	private Synchronizer<World> defaultWorld;
	
	public WorldsManager(Server server) {
		this.server = server;
	}
	
	public Synchronizer<World> createWorld(String name, int width, int height) {
		if(worlds.containsKey(name)) {
			throw new IllegalStateException("there is a world with this name already");
		}
		World world = new World(server.getSynchronizer(), name, width, height);
		Synchronizer<World> worldSyncer = new Synchronizer<>(world);
		if(defaultWorld == null) {
			defaultWorld = worldSyncer;
		}
		this.worlds.put(name, worldSyncer);
		return worldSyncer;
	}
	
	public Synchronizer<World> getWorld(String name) {
		return worlds.get(name);
	}
	
	public Synchronizer<World> getDefaultWorld() {
		return defaultWorld;
	}
	
}
