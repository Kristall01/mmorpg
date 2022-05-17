package hu.kristall.rpg.world.path.plan;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.path.Path;
import hu.kristall.rpg.world.path.ZigzagPosition;

import java.util.Collection;
import java.util.List;

public interface PathFinder {
	
	Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos);
	
	static Path generatePath(List<Position> positions, long startTimeNanos, double cellsPerSec) {
		if(positions == null) {
			return null;
		}
		return new Path(positions.get(positions.size()-1), positions, new ZigzagPosition(positions, startTimeNanos, cellsPerSec), startTimeNanos);
	}
	
}
