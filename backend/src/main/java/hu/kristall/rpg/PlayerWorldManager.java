package hu.kristall.rpg;

import hu.kristall.rpg.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//server thread
public class PlayerWorldManager {
	
	private final Map<Player, Synchronizer<World>> map = new ConcurrentHashMap<>();
	private final Server server;
	
	public PlayerWorldManager(Server server) {
		this.server = server;
	}
	
	/*public boolean joinWorld(Player player, Synchronizer<World> asyncWorld) {
		boolean b = player.tryLockWorld();
		if(!b) {
			return false;
		}
		asyncWorld.sync(c -> {
			c.joinPlayer(player);
			map.put(player, asyncWorld);
			player.unlockWorld();
		});
		return true;
	}
	
	public boolean leaveWorld(Player player) {
		boolean b = player.tryLockWorld();
		if(!b) {
			return false;
		}
		Synchronizer<World> asyncWorld = map.get(player);
		if(asyncWorld == null) {
			player.unlockWorld();
			return true;
		}
		asyncWorld.sync(c -> {
			c.leavePlayer(player);
			map.remove(player);
			player.unlockWorld();
		});
		return true;
	}*/
	
	public boolean changeWorld(Player player, Synchronizer<World> asyncNewWorld) {
		boolean b = player.tryLockWorld();
		if(!b) {
			return false;
		}
		Synchronizer<World> asyncOldWorld = map.get(player);
		if(asyncOldWorld != null) {
			asyncOldWorld.sync(c -> {
				c.leavePlayer(player);
				player.setAsyncEntity(null);
				map.remove(player);
				if(asyncNewWorld != null) {
					asyncNewWorld.sync(newWorld -> {
						map.put(player, asyncNewWorld);
						player.setAsyncEntity(newWorld.joinPlayer(player));
						player.unlockWorld();
					});
				}
			});
			return true;
		}
		else {
			asyncNewWorld.sync(newWorld -> {
				map.put(player, asyncNewWorld);
				player.setAsyncEntity(newWorld.joinPlayer(player));
				player.unlockWorld();
			});
			return true;
		}
	}
	
}
