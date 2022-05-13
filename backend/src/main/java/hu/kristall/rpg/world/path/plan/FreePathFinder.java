package hu.kristall.rpg.world.path.plan;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.path.LinearPosition;
import hu.kristall.rpg.world.path.Path;

import java.util.List;

public class FreePathFinder implements PathFinder {
	
	@Override
	public Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		return new Path(to, List.of(from, to), new LinearPosition(from, to, cellsPerSec, startTimeNanos), startTimeNanos);
	}
	
}
