package hu.kristall.rpg.world.path;

import hu.kristall.rpg.Position;

import java.util.Collection;

public class Path {
	
	private Collection<Position> collection;
	private PositionFunction posiFn;
	private Position target;
	private long startNanos;
	
	public Path(Position target, Collection<Position> collection, PositionFunction posiFn, long startNanos) {
		this.collection = collection;
		this.posiFn = posiFn;
		this.target = target;
		this.startNanos = startNanos;
	}
	
	public long getStartNanos() {
		return startNanos;
	}
	
	public Position getTarget() {
		return target;
	}
	
	public Collection<Position> getCollection() {
		return collection;
	}
	
	public PositionFunction getPosiFn() {
		return posiFn;
	}
}
