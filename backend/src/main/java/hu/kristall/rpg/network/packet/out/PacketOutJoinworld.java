package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.World;

public class PacketOutJoinworld extends PacketOut{
	
	String tileGrid;
	int width, height;
	double spawnX;
	double spawnY;
	
	public PacketOutJoinworld(World world, Position spawnPosition) {
		super("joinworld");
		this.width = world.getWidth();
		this.height = world.getHeight();
		this.spawnX = spawnPosition.getX();
		this.spawnY = spawnPosition.getY();
	}
}
