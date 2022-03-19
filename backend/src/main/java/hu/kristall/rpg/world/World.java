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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class World extends SynchronizedObject<World> {
	
	protected final Tile[] tileMap;
	protected Tile defaultTile;
	protected final int width, height;
	protected HashMap<AsyncPlayer, WorldPlayer> worldPlayers = new HashMap<>();
	protected HashMap<Integer, Entity> worldEntities = new HashMap<>();
	protected String name;
	protected AsyncServer asyncServer;
	protected int nextEntityID = 0;
	protected List<String> bakedMapSerialize;
	protected Logger logger;
	private boolean shuttingDown = false;
	private boolean defaultWorld;
	private List<Portal> portals = new ArrayList();
	
	public World(AsyncServer serverSynchronizer, boolean defaultWorld, String name, int width, int height) {
		super("world-"+name);
		
		this.defaultWorld = defaultWorld;
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
		if(name.contentEquals("w0")) {
			addPortal(new Portal(new Position(1, 1), "w1"));
		}
		else if(name.contentEquals("w1")) {
			addPortal(new Portal(new Position(1, 1), "w2"));
		}
		else if(name.contentEquals("w2")) {
			addPortal(new Portal(new Position(1, 1), "w0"));
		}
		
		getTimer().scheduleAtFixedRate(this::checkPortals, 0, 250);
	}
	
	public void addPortal(Portal p) {
		this.portals.add(p);
		this.broadcastPacket(new PacketOutPortalSpawn(p));
	}
	
	private void checkPortals() {
		for (WorldPlayer worldPlayer : worldPlayers.values()) {
			if(!worldPlayer.hasEntity()) {
				continue;
			}
			for (Portal portal : portals) {
				if(portal.checkCollision(worldPlayer.getEntity())) {
					worldPlayer.startChangingWorld(portal.getTargetWorldName());
				}
			}
		}
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
		for (WorldPlayer value : worldPlayers.values()) {
			entity.sendStatusFor(value.getAsyncPlayer().connection);
		}
	}
	
	public Synchronizer<WorldPlayer> joinPlayer(AsyncPlayer player) {
		try {
			//sync world state to joining player
			Position pos = new Position(3,3);
			PlayerConnection connectingConnection = player.connection;
			connectingConnection.sendPacket(new PacketOutJoinworld(this, pos));
			for (Entity e : this.worldEntities.values()) {
				e.sendStatusFor(connectingConnection);
			}
			for (Portal portal : portals) {
				connectingConnection.sendPacket(new PacketOutPortalSpawn(portal));
			}
			
			//sync done
			
			WorldPlayer wp = new WorldPlayer(this, player);
			worldPlayers.put(player, wp);
			EntityHuman h = wp.spawnTo(pos);
			connectingConnection.sendPacket(new PacketOutFollowEntity(h));
			broadcastMessage("§e" + player.name + " csatlakozott");
			
			//player.followEntity(p.getID());
			
			//broadcast join
			//send map packets
			//list entities
			//etc
			
			/*
			getTimer().schedule(() -> {
				WorldPlayer wp0 = worldPlayers.get(player);
				if(wp0 != null && wp0.hasEntity()) {
					wp0.getEntity().damage((20));
				}
			}, 2000 );
			 */
			
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
	
	public void leavePlayer(AsyncPlayer player) {
		WorldPlayer oldEntity = worldPlayers.remove(player);
		player.connection.sendPacket(new PacketOutLeaveWorld());
		if(oldEntity.getEntity() != null) {
			oldEntity.getEntity().remove();
		}
		oldEntity.getSynchronizer().changeObject(null);
		broadcastMessage("§e"+player.name+ " lelépett");
		if(shuttingDown && worldPlayers.size() == 0) {
			super.shutdown();
		}
	}
	
	public void broadcastMessage(String message) {
		final String worldName = this.name;
		logger.info(message);
		broadcastPacket(new PacketOutChat(message));
	}
	
	public void broadcastPacket(PacketOut out) {
		for (WorldPlayer wp : worldPlayers.values()) {
			wp.getAsyncPlayer().connection.sendPacket(out);
		}
	}
	
	public AsyncServer getAsyncServer() {
		return asyncServer;
	}
	
	public void shutdownWorld() {
		/*if(this.shuttingDown) {
			return;
		}
		if(this.defaultWorld) {
			if(!asyncServer.isShuttingDown()) {
				throw new IllegalStateException("default world cannot be shutdown unless server is stopping");
			}
			shuttingDown = true;
			if(this.worldPlayers.size() == 0) {
				super.shutdown();
				return;
			}
			for (WorldPlayer wp : worldPlayers.values()) {
				wp.getAsyncPlayer().sync(p -> p.kick("Szerver leállás"));
			}
			return;
		}
		shuttingDown = true;
/*		if(this.defaultWorld) {
			if(asyncServer.shuttingDown)
		}
		shuttingDown = true;
		shutdownWorld0();*/
		if(!asyncServer.isShuttingDown()) {
			throw new IllegalStateException("worlds cannot be shut down while server is running");
		}
		super.shutdown();
	}
	
	public String getName() {
		return name;
	}
	
	public Position fixValidate(Position to) {
		double targetX = to.getX(), targetY = to.getY();
		if(targetX < 0) {
			targetX = 0;
		}
		else if(targetX > width) {
			targetX = width;
		}
		if(targetY < 0) {
			targetY = 0;
		}
		else if(targetY > height) {
			targetY = height;
		}
		return new Position(targetX,targetY);
	}
	
}
