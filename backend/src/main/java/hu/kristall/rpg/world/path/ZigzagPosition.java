package hu.kristall.rpg.world.path;

import hu.kristall.rpg.Position;

import java.util.ArrayList;
import java.util.List;

public class ZigzagPosition implements PositionFunction {
	
	private int index = 0;
	private Position target;
	private long moveEndNanos;
	private Long[] timePoints;
	private Position[] points;
	
	public ZigzagPosition(List<Position> pointList, long startTimeNanos, double cellsPerSec) {
		this.points = pointList.toArray(new Position[0]);
		target = points[points.length - 1];
		double totalDist = 0;
		int i = 0;
		moveEndNanos = startTimeNanos;
		ArrayList<Long> timePoints = new ArrayList<>();
		while (true) {
			long t = (long)(totalDist / cellsPerSec * 1000000000);
			moveEndNanos += t;
			timePoints.add(startTimeNanos + t);
			if(i >= points.length - 1) {
				break;
			}
			totalDist += Position.distance(points[i], points[i + 1]);
			i++;
		}
		this.timePoints = timePoints.toArray(new Long[0]);
	}
	
	@Override
	public Position getCurrentPosition() {
		long currentTimeMs = System.nanoTime();
		if(currentTimeMs > moveEndNanos) {
			return target;
		}
		int i = index;
		for(;i < timePoints.length - 1; ++i) {
			if(currentTimeMs < timePoints[i+1]) {
				break;
			}
		}
		index = i;
		if(i == timePoints.length - 1) {
			return target;
		}
			
		double xDiff = points[i+1].getX() - points[i].getX();
		double yDiff = points[i+1].getY() - points[i].getY();
		return new Position(
			points[i].getX()+ xDiff * ((double)(currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i])),
			points[i].getY() + yDiff * ((double)(currentTimeMs - timePoints[i]) / (timePoints[i+1] - timePoints[i]))
		);
	}
	
	@Override
	public boolean moving() {
		return System.nanoTime() < moveEndNanos;
	}

}
