package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.path.Path;

import java.util.ArrayList;
import java.util.List;

public class PacketOutMoveentity extends PacketOut {
	
	private List<Double> x = new ArrayList<>();
	private List<Double> y = new ArrayList<>();
	int id;
	long startNanos;
	
	public PacketOutMoveentity(int ID, Path path, long startNanos) {
		super("moveentity");

		this.id = ID;
		this.startNanos = startNanos;
		for (Position position : path.getCollection()) {
			x.add(position.getX());
			y.add(position.getY());
		}
	}
	
}
