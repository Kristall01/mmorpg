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
}
