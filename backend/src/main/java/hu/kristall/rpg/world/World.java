package hu.kristall.rpg.world;

import hu.kristall.rpg.*;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.*;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutDespawnItem;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSetInventory;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSpawnItem;
import hu.kristall.rpg.persistence.SavedPlayer;
import hu.kristall.rpg.sync.SynchronizedObject;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.*;
import hu.kristall.rpg.world.path.ConstantPosition;
import hu.kristall.rpg.world.path.Path;
import hu.kristall.rpg.world.path.plan.PathFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class World extends SynchronizedObject<World> {
	
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
	private List<Portal> portals = new ArrayList<>();
	private Map<GeneratedID<FloatingItem>, FloatingItem> floatingItems = new HashMap<>();
	private PathFinder pathFinder;
	private Position bottomRightPosition;
	private Position topLeftPosition = new Position(0,0);
	private ItemMap itemMap;
	
	public World(AsyncServer serverSynchronizer, String name, int width, int height, String[] tileGrid, PathFinder pathFinder, List<EntitySpawner> entitySpawners, ItemMap itemMap) {
		super("world-"+name);
		this.itemMap = itemMap;
		this.pathFinder = pathFinder;
		
		this.logger = LoggerFactory.getLogger("world-"+name);
		
		this.asyncServer = serverSynchronizer;
		this.name = name;
		
		this.width = width;
		this.height = height;
		
		bottomRightPosition = new Position(width, height);
		
		if(tileGrid == null) {
			tileGrid = new String[width*height];
			Arrays.fill(tileGrid, "GRASS");
		}
		this.bakedMapSerialize = List.of(tileGrid);
		
		getTimer().scheduleAtFixedRate((c) -> this.checkPortals(), 0, 250);
		for (EntitySpawner entitySpawner : entitySpawners) {
			entitySpawner.registerTo(this);
		}
	}
	
	public ItemMap getItemMap() {
		return itemMap;
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
					worldPlayer.startChangingWorld(portal.getTargetWorldName(), portal.getTargetPosition());
				}
			}
		}
	}
	
	public Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		return pathFinder.findPath(from, to, cellsPerSec, startTimeNanos);
	}
	
	public Position getRandomPositionNear(Position pos, double minDistance, double maxDistance) {
		double randDistance = Utils.random.nextDouble()*(maxDistance-minDistance)+minDistance;
		double xVector = (Utils.random.nextDouble()*2-1)*randDistance;
		double yVector = (Utils.random.nextDouble()*2-1)*randDistance;
		return Path.fixPosition(topLeftPosition, pos.add(xVector, yVector), bottomRightPosition);
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
		return this.spawnEntity(type, pos, null);
	}
	
	public Entity spawnEntity(EntityType type, Position pos, Object optional) {
		Entity createdEntity = null;
		switch(type) {
			case HUMAN: {
				createdEntity = EntityHuman.ofData(this, getNextEntityID(), pos, optional);
				break;
			}
			case DUMMY: {
				createdEntity = new EntityDummy(this, getNextEntityID(), pos);
				break;
			}
			case SLIME: {
				createdEntity = EntitySlime.createSlime(this,getNextEntityID(),pos, optional);
				break;
			}
			case SKELETON: {
				createdEntity = new EntitySkeleton(this, getNextEntityID(), pos);
				break;
			}
			case OGRE: {
				createdEntity = new EntityOgre(this, getNextEntityID(), pos);
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
	
	public Synchronizer<WorldPlayer> joinPlayer(AsyncPlayer player, SavedPlayer savedPlayer, Position pos) {
		try {
			//sync world state to joining player
			if(pos == null) {
				pos = new Position(width >> 1, height >> 1);
			}
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
		if(item == null) {
			return null;
		}
		GeneratedID<FloatingItem> itemID = nextItemID.get();
		FloatingItem floatingItem = new FloatingItem(this, itemID, pos, item);
		floatingItems.put(itemID, floatingItem);
		broadcastPacket(new PacketOutSpawnItem(floatingItem));
		return floatingItem;
	}
	
	public FloatingItem spawnItemNear(Item item, Position pos) {
		return spawnItem(item, getRandomPositionNear(pos, 0,0.5));
	}
	
	
	public void cleanRemovedEntity(Entity e) {
		if(e.isRemoved()) {
			worldEntities.remove(e.getID());
			broadcastPacket(new PacketOutDespawnEntity(e));
		}
	}
	
	public Collection<Entity> getEntities() {
		return Collections.unmodifiableCollection(this.worldEntities.values());
	}
	
	public SavedPlayer leavePlayer(AsyncPlayer player) {
		WorldPlayer oldWP = worldPlayers.remove(player);
		if(oldWP == null) {
			return null;
		}
		oldWP.quit();
		player.connection.sendPacket(new PacketOutLeaveWorld());
		EntityHuman h = oldWP.getEntity();
		SavedPlayer savedPlayer = null;
		if(h != null) {
			savedPlayer = h.structuredClone();
			h.remove();
		}
		oldWP.getSynchronizer().changeObject(null);
		broadcastMessage("§e"+player.name+ " lelépett");
		if(shuttingDown && worldPlayers.size() == 0) {
			super.shutdown();
		}
		return savedPlayer;
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
	
	/*public SavedLevel serialize() {
		return new SavedLevel(this.name, this.width, this.height, null, new ArrayList<>(), this.portals.stream().map(Portal::serialize).collect(Collectors.toList()));
	}*/
	
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
	
	public Path idlePath(Position pos) {
		return new Path(pos, List.of(pos), new ConstantPosition(pos), System.nanoTime());
	}
	
}
