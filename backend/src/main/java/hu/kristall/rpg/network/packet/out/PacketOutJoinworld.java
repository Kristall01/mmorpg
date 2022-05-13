package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

import java.util.List;

public class PacketOutJoinworld extends PacketOut {
	
	List<String> tileGrid;
	int width, height;
	double spawnX;
	double spawnY;
	
	private PacketOutJoinworld() {
		super("joinworld");
	}
	
	public PacketOutJoinworld(List<String> tileGrid, int worldHeight, int worldWidth, Position spawnPosition) {
		this();
		
		this.tileGrid = tileGrid;
		this.width = worldWidth;
		this.height = worldHeight;
		this.spawnX = spawnPosition.getX();
		this.spawnY = spawnPosition.getY();
	}
	
	public PacketOutJoinworld(World world, Position spawnPosition) {
		this(world.serializeTileGrid(), world.getHeight(), world.getWidth(), spawnPosition);
	}
	
}
