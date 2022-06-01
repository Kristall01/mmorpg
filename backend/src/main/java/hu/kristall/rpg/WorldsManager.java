package hu.kristall.rpg;

import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.EntitySpawner;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.plan.PathFinder;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorldsManager {
	
	private final HashMap<String, Synchronizer<World>> worlds = new HashMap<>();
	private Server server;
	private Synchronizer<World> defaultWorld;
	private String defaultWorldName;
	
	public WorldsManager(Server server, String defaultWorldName) {
		this.server = server;
		this.defaultWorldName = defaultWorldName;
	}
	
	public Synchronizer<World> createWorld(String name, int width, int height, String[] layers, PathFinder pathFinder, List<EntitySpawner> entitySpawners, Position spawnPosition) {
		if(worlds.containsKey(name)) {
			throw new IllegalStateException(server.getLang().getMessage("worldmanager.create.name-taken"));
		}
		boolean defaultWorld = this.defaultWorld == null || defaultWorldName.contentEquals(name);
		World world = new World(server.getSynchronizer(), name, width, height, layers, pathFinder, entitySpawners, server.getItemMap(), spawnPosition);
		Synchronizer<World> worldSyncer = world.getSynchronizer();
		this.worlds.put(name, worldSyncer);
		if(defaultWorld) {
			this.defaultWorld = worldSyncer;
		}
		return worldSyncer;
	}
	
	public Synchronizer<World> getWorld(String name) {
		return worlds.get(name);
	}
	
	public Synchronizer<World> getDefaultWorld() {
		return defaultWorld;
	}
	
	public Collection<String> getWorldNames() {
		return Collections.unmodifiableCollection(this.worlds.keySet());
	}
	
	public Set<Map.Entry<String, Synchronizer<World>>> getWorlds() {
		return Collections.unmodifiableSet(this.worlds.entrySet());
	}
	
	public void shutdown() {
		server.getLogger().info(server.getLang().getMessage("worldmanager.shutting"));
		List<Future<String>> shutdownTasks = new ArrayList<>();
		for (Synchronizer<World> world : worlds.values()) {
			try {
				shutdownTasks.add(world.syncCompute(w -> {
					String name = w.getName();
					w.shutdownWorld();
					return name;
				}));
			}
			catch (Synchronizer.TaskRejectedException e) {
				//world wont be shut down before this operation
				e.printStackTrace();
			}
		}
		for (Future<?> shutdownTask : shutdownTasks) {
			try {
				server.getLogger().info(server.getLang().getMessage("worldmanager.world-shut", String.valueOf(shutdownTask.get())));
			}
			catch (InterruptedException | ExecutionException e) {
				server.getLogger().warn(server.getLang().getMessage("worldmanager.world-shut-interrupted"), e);
				e.printStackTrace();
			}
		}
	}
}
