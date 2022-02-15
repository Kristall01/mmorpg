package hu.kristall.rpg;

import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
		Synchronizer<World> worldSyncer = world.getSynchronizer();
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
	
	public void shutdown() {
		server.getLogger().info("Shutting down worlds");
		List<Future<String>> shutdownTasks = new ArrayList<>();
		for (Synchronizer<World> world : worlds.values()) {
			shutdownTasks.add(world.syncCompute(w -> {
				String name = w.getName();
				w.shutdown();
				return name;
			}));
		}
		for (Future<?> shutdownTask : shutdownTasks) {
			try {
				server.getLogger().info("World "+shutdownTask.get()+" shut down");
			}
			catch (InterruptedException | ExecutionException e) {
				server.getLogger().warn("world shutdown interrupted", e);
				e.printStackTrace();
			}
		}
	}
}
