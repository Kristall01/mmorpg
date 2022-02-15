package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.path.Path;

import java.util.ArrayList;
import java.util.List;

public class PacketOutMoveentity extends PacketOut {
	
	private List<Double> x;
	private List<Double> y;
	int id;
	long startNanos;
	
	private PacketOutMoveentity(int entityID)  {
		super("moveentity");
		this.id = entityID;
	}
	
	public PacketOutMoveentity(int entityID, long startNanos, List<Double> x, List<Double> y) {
		this(entityID);
		this.startNanos = startNanos;
		this.x = x;
		this.y = y;
	}
	
	public PacketOutMoveentity(Entity entity) {
		this(entity.getID());
		
		Path p = entity.getLastPath();
		this.startNanos = p.getStartNanos();
		
		this.x = new ArrayList<>();
		this.y = new ArrayList<>();
		
		for (Position position : p.getCollection()) {
			x.add(position.getX());
			y.add(position.getY());
		}
		
	}
	
}
