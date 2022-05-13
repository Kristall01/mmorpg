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
	
	public static Position fixPosition(Position min, Position pos, Position max) {
		double x = pos.getX(), y = pos.getY();
		
		double maxX = max.getX();
		double maxY = max.getY();
		
		double minX = min.getX();
		double minY = min.getY();
		
		boolean changed = false;
		if(x < minX) {
			changed = true;
			x = minX;
		}
		else if(x > maxX) {
			changed = true;
			x = maxX;
		}
		
		if(y < minY) {
			changed = true;
			y = minY;
		}
		else if(y > maxY) {
			changed = true;
			y = maxY;
		}
		if(!changed) {
			return pos;
		}
		return new Position(x, y);
	}
}
