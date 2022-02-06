package hu.kristall.rpg.world;

import hu.kristall.rpg.*;
import hu.kristall.rpg.network.packet.out.*;
import hu.kristall.rpg.network.phase.PhasePlay;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityPlayer;
import hu.kristall.rpg.world.path.LinearPosition;
import hu.kristall.rpg.world.path.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class World extends SynchronizedObject<World> {

	private final Tile[] tileMap;
	private Tile defaultTile;
	private final int width, height;
	private HashMap<Player, EntityPlayer> playerEntities = new HashMap<>();
	private HashMap<Integer, Entity> worldEntities = new HashMap<Integer, Entity>();
	private String name;
	private Synchronizer<Server> asyncServer;
	private int nextEntityID = 0;
	private String bakedMapSerialize;
	
	public World(Synchronizer<Server> serverSynchronizer, String name, int width, int height) {
		this.asyncServer = serverSynchronizer;
		this.name = name;
		
		this.tileMap = new Tile[width*height];
		this.width = width;
		this.height = height;
		
		Arrays.fill(tileMap, Tile.GRASS);
		
		StringBuilder b = new StringBuilder("[");
		for (int i = 0; i < tileMap.length; i++) {
			b.append('"').append(tileMap[i].ID).append("\",");
		}
		b.setCharAt(b.length()-1, ']');
		this.bakedMapSerialize = b.toString();
	}
	
	public Tile getTileAt(int x, int y) {
		if(x < 0 || y < 0 || x > width || y > height) {
			return Tile.WATER;
		}
		return tileMap[width*y + x];
	}
	
	public Path interpolatePath(Position from, Position to, double cellsPerSec, double startTimeNanos) {
		return new Path(List.of(from, to), new LinearPosition(from, to, cellsPerSec, startTimeNanos));
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	private int getNextEntityID() {
		return nextEntityID++;
	}
	
	public Synchronizer<EntityPlayer> joinPlayer(Player player) {
		try {
			EntityPlayer p = new EntityPlayer(this, getNextEntityID(), player, new Position(3, 3));
			playerEntities.put(player, p);
			PhasePlay creator = p.getPlayer().getCreator();
			
			creator.sendPacket(new PacketOutJoinworld(this, new Position(3, 3)));
			
			for (Entity asd : worldEntities.values()) {
				creator.sendPacket(new PacketOutSpawnEntity(asd));
			}
			
			worldEntities.put(p.getID(), p);
			
			broadcastPacket(new PacketOutSpawnEntity(p));
			broadcastMessage("§e" + player.getName() + " csatlakozott");
			
			player.getCreator().sendPacket(new PacketOutFollowEntity(p));
			
			//broadcast join
			//send map packets
			//list entities
			//etc
			
			return p.getSynchronizer();
		}
		catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public String serializeTileGrid() {
		return bakedMapSerialize;
	}
	
	public void leavePlayer(Player player) {
		EntityPlayer oldEntity = playerEntities.remove(player);
		worldEntities.remove(oldEntity.getID());
		oldEntity.getSynchronizer().changeObject(null);
		broadcastMessage("§e"+player.getName()+ " lelépett");
		broadcastPacket(new PacketOutDespawnEntity(oldEntity));
		//broadcast leave
		//send map leave packet
		//etc
	}
	
	public void broadcastMessage(String message) {
		final String worldName = this.name;
		asyncServer.sync(srv -> srv.getCommandMap().getConsoleCommandSender().sendMessage("[@"+worldName+"] "+message));
		for (EntityPlayer value : playerEntities.values()) {
			value.getPlayer().sendMessage(message);
		}
	}
	
	public void broadcastPacket(PacketOut packet) {
		for (EntityPlayer value : playerEntities.values()) {
			value.getPlayer().getCreator().sendPacket(packet);
		}
	}
	
	public Synchronizer<Server> getAsyncServer() {
		return asyncServer;
	}
	
}
