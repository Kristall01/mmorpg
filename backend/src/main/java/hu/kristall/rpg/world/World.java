package hu.kristall.rpg.world;

import hu.kristall.rpg.*;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.*;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutDespawnItem;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSetInventory;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSpawnItem;
import hu.kristall.rpg.persistence.SavedLevel;
import hu.kristall.rpg.persistence.SavedPlayer;
import hu.kristall.rpg.sync.SynchronizedObject;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.EntityType;
import hu.kristall.rpg.world.path.LinearPosition;
import hu.kristall.rpg.world.path.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class World extends SynchronizedObject<World> {
	
	protected final Tile[] tileMap;
	protected Tile defaultTile;
	protected final int width, height;
	protected HashMap<AsyncPlayer, WorldPlayer> worldPlayers = new HashMap<>();
	protected HashMap<Integer, Entity> worldEntities = new HashMap<>();
	protected String name;
	protected AsyncServer asyncServer;
	protected int nextEntityID = 0;
	private IdGenerator<FloatingItem> nextItemID = new IdGenerator<>();
	protected List<String> bakedMapSerialize;
	protected Logger logger;
	private boolean shuttingDown = false;
	private boolean defaultWorld;
	private List<Portal> portals = new ArrayList<>();
	private Map<GeneratedID<FloatingItem>, FloatingItem> floatingItems = new HashMap<>();
	
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
	
	public Collection<FloatingItem> getItems() {
		return Collections.unmodifiableCollection(this.floatingItems.values());
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
	
	public Entity spawnEntity(EntityType type, Position pos, Object optional) {
		Entity createdEntity = null;
		switch(type) {
			case HUMAN: {
				createdEntity = EntityHuman.ofData(this, getNextEntityID(), pos, optional);
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
	
	public Synchronizer<WorldPlayer> joinPlayer(AsyncPlayer player, SavedPlayer savedPlayer) {
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
			
			for(FloatingItem item : this.floatingItems.values()) {
				connectingConnection.sendPacket(new PacketOutSpawnItem(item));
			}
			
			//sync done
			
			WorldPlayer wp = new WorldPlayer(this, player);
			worldPlayers.put(player, wp);
			EntityHuman h = wp.spawnTo(pos, savedPlayer);
			connectingConnection.sendPacket(new PacketOutFollowEntity(h));
			connectingConnection.sendPacket(new PacketOutSetInventory(h.getInventory()));
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
	
	public void cleanRemovedItem(FloatingItem item) {
		if(item.isRemoved()) {
			floatingItems.remove(item.getID());
			broadcastPacket(new PacketOutDespawnItem(item));
		}
	}
	
	public FloatingItem spawnItem(Item item, Position pos) {
		GeneratedID<FloatingItem> itemID = nextItemID.get();
		FloatingItem floatingItem = new FloatingItem(this, itemID, pos, item);
		floatingItems.put(itemID, floatingItem);
		broadcastPacket(new PacketOutSpawnItem(floatingItem));
		return floatingItem;
	}
	
	public void cleanRemovedEntity(Entity e) {
		if(e.isRemoved()) {
			worldEntities.remove(e.getID());
			broadcastPacket(new PacketOutDespawnEntity(e));
		}
	}
	
	public SavedPlayer leavePlayer(AsyncPlayer player) {
		WorldPlayer oldWP = worldPlayers.remove(player);
		if(oldWP == null) {
			return null;
		}
		player.connection.sendPacket(new PacketOutLeaveWorld());
		EntityHuman h = oldWP.getEntity();
		SavedPlayer savedPlayer = null;
		if(h != null) {
			savedPlayer = h.structuredClone();
			oldWP.getEntity().remove();
		}
		oldWP.getSynchronizer().changeObject(null);
		broadcastMessage("§e"+player.name+ " lelépett");
		if(shuttingDown && worldPlayers.size() == 0) {
			super.shutdown();
		}
		//return serializedHuman;
		return null;
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
	
	public SavedLevel serialize() {
		return new SavedLevel(this.name, this.width, this.height, null, new ArrayList<>(), this.portals.stream().map(Portal::serialize).collect(Collectors.toList()));
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
