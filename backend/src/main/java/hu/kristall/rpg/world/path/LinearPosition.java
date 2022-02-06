package hu.kristall.rpg.world.path;

import hu.kristall.rpg.Position;

public class LinearPosition implements PositionFunction {
	
	private final Position from, to;
	private final double startTimeNanos;
	private final double xDiff;
	private final double yDiff;
	private final double totalTime;
	private final double endTime;
	
	public LinearPosition(Position from, Position to, double cellsPerSec, double startTimeNanos) {
		this.from = from;
		this.to = to;
		this.startTimeNanos = startTimeNanos;
		
		this.xDiff = to.getX() - from.getX();
		this.yDiff = to.getY() - from.getY();
		
		double totalDistance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
		this.totalTime = totalDistance / cellsPerSec * 1000000000;
		this.endTime = startTimeNanos + totalTime;
	}
	
	public Position getCurrentLocation() {
		long currentTime = System.nanoTime();
		if(currentTime > endTime) {
			return to;
		}
		return this.from.add(
			xDiff * ((currentTime - startTimeNanos) / totalTime),
			yDiff * ((currentTime - startTimeNanos) / totalTime));
	}
	
}
