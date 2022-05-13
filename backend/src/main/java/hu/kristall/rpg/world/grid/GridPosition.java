package hu.kristall.rpg.world.grid;

import hu.kristall.rpg.Position;

import java.util.Objects;

public class GridPosition {
	
	public final int x, y;
	
	public GridPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public GridPosition add(int x, int y) {
		return new GridPosition(this.x + x, this.y + y);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof GridPosition)) return false;
		GridPosition that = (GridPosition) o;
		return x == that.x && y == that.y;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	public static Double rads(GridPosition p0, GridPosition p1) {
		if(p0.equals(p1)) {
			return null;
		}
		double dist = distance(p0, p1);
		double xDiff = (p0.x - p1.x)/dist;
		double yDiff = p0.y - p1.y;
		double rads = Math.acos(xDiff);
		if(yDiff < 0) {
			rads = (Math.PI*2) - rads;
		}
		return rads;
	}
	
	public Position toPosition() {
		return new Position(x+0.5, y+0.5);
	}
	
	public static double distance(GridPosition p0, GridPosition p1) {
		return Math.sqrt(Math.pow(p0.x - p1.x, 2) + Math.pow(p0.y - p1.y, 2));
	}
}
