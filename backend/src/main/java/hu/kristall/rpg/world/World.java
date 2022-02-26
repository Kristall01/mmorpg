package hu.kristall.rpg.world;

import hu.kristall.rpg.*;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.*;
import hu.kristall.rpg.sync.SynchronizedObject;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.EntityType;
import hu.kristall.rpg.world.path.LinearPosition;
import hu.kristall.rpg.world.path.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class World extends SynchronizedObject<World> {

	private final Tile[] tileMap;
	private Tile defaultTile;
	private final int width, height;
	private HashMap<Player, WorldPlayer> worldPlayers = new HashMap<>();
	private HashMap<Integer, Entity> worldEntities = new HashMap<>();
	private String name;
	private Synchronizer<Server> asyncServer;
	private int nextEntityID = 0;
	private List<String> bakedMapSerialize;
	private Logger logger;
	
	public World(Synchronizer<Server> serverSynchronizer, String name, int width, int height) {
		super("world-"+name);
		this.logger = LoggerFactory.getLogger("world-"+name);
		
		this.asyncServer = serverSynchronizer;
		this.name = name;
		
		this.tileMap = new Tile[width*height];
		this.width = width;
		this.height = height;
		
		Arrays.fill(tileMap, Tile.GRASS);
		String[] s = new String[tileMap.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = tileMap[i].name();
		}
		this.bakedMapSerialize = List.of(s);
	}
	
	public Tile getTileAt(int x, int y) {
		if(x < 0 || y < 0 || x > width || y > height) {
			return Tile.WATER;
		}
		return tileMap[width*y + x];
	}
	
	public Path interpolatePath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		return new Path(to, List.of(from, to), new LinearPosition(from, to, cellsPerSec, startTimeNanos), startTimeNanos);
	}
	
	public int getWidth() {
		return width;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public int getHeight() {
		return height;
	}
	
	private int getNextEntityID() {
		return nextEntityID++;
	}
	
	public Entity spawnEntity(EntityType type, Position pos) {
		Entity createdEntity = null;
		switch(type) {
			case HUMAN: {
				createdEntity = new EntityHuman(this, getNextEntityID(), pos);
				break;
			}
		}
		if(createdEntity == null) {
			return null;
		}
		addEntity(createdEntity);
		return createdEntity;
	}
	
	private void addEntity(Entity entity) {
		worldEntities.put(entity.getID(), entity);
		broadcastPacket(new PacketOutSpawnEntity(entity));
	}
	
	public Synchronizer<WorldPlayer> joinPlayer(Player player) {
		try {
			//sync world state to joining player
			Position pos = new Position(3,3);
			PlayerConnection connectingConnection = player.getConnection();
			connectingConnection.sendPacket(new PacketOutJoinworld(this, pos));
			for (Entity e : this.worldEntities.values()) {
				e.sendStatusFor(connectingConnection);
			}
			
			//sync done
			
			WorldPlayer wp = new WorldPlayer(this, player);
			worldPlayers.put(player, wp);
			EntityHuman h = wp.spawnTo(pos);
			connectingConnection.sendPacket(new PacketOutFollowEntity(h));
			broadcastMessage("§e" + player.getName() + " csatlakozott");
			
			//player.followEntity(p.getID());
			
			//broadcast join
			//send map packets
			//list entities
			//etc
			
			return wp.getSynchronizer();
		}
		catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public List<String> serializeTileGrid() {
		return bakedMapSerialize;
	}
	
	public void cleanRemovedEntity(Entity e) {
		if(e.isRemoved()) {
			worldEntities.remove(e.getID());
			broadcastPacket(new PacketOutDespawnEntity(e));
		}
	}
	
	public void leavePlayer(Player player) {
		WorldPlayer oldEntity = worldPlayers.remove(player);
		player.getConnection().sendPacket(new PacketOutLeaveWorld());
		if(oldEntity.getEntity() != null) {
			oldEntity.getEntity().remove();
		}
		oldEntity.getSynchronizer().changeObject(null);
		broadcastMessage("§e"+player.getName()+ " lelépett");
	}
	
	public void broadcastMessage(String message) {
		final String worldName = this.name;
		logger.info(message);
		broadcastPacket(new PacketOutChat(message));
	}
	
	public void broadcastPacket(PacketOut out) {
		for (WorldPlayer wp : worldPlayers.values()) {
			wp.getPlayer().getConnection().sendPacket(out);
		}
	}
	
	public Synchronizer<Server> getAsyncServer() {
		return asyncServer;
	}
	
	public void shutdown() {
		for (WorldPlayer wp : worldPlayers.values()) {
			wp.getPlayer().kick("A világ amiben tartózkodtál leállt.");
		}
		getSynchronizer().changeObject(null);
		super.shutdown();
	}
	
	public String getName() {
		return name;
	}
	
}
