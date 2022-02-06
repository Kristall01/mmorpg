package hu.kristall.rpg.world.path;

import hu.kristall.rpg.Position;

import java.util.Collection;

public class Path {
	
	private Collection<Position> collection;
	private PositionFunction posiFn;
	
	public Path(Collection<Position> collection, PositionFunction posiFn) {
		this.collection = collection;
		this.posiFn = posiFn;
	}
	
	public Collection<Position> getCollection() {
		return collection;
	}
	
	public PositionFunction getPosiFn() {
		return posiFn;
	}
}
