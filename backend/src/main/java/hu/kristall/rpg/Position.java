package hu.kristall.rpg;

import java.util.Objects;

public class Position {
	
	private final double x, y;
	
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Position add(double x, double y) {
		return new Position(this.x + x, this.y + y);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return Double.compare(position.x, x) == 0 && Double.compare(position.y, y) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	public static double rads(Position p0, Position p1) {
		if(p0.equals(p1)) {
			return 0;
		}
		double dist = distance(p0, p1);
		double xDiff = (p0.getX() - p1.getX())/dist;
		double yDiff = p0.getY() - p1.getY();
		double rads = Math.acos(xDiff);
		if(yDiff < 0) {
			rads = (Math.PI*2) - rads;
		}
		return rads;
	}
	
	public static double distance(Position p0, Position p1) {
		return Math.sqrt(Math.pow(p0.getX() - p1.getX(), 2) + Math.pow(p0.getY() - p1.getY(), 2));
	}
}
