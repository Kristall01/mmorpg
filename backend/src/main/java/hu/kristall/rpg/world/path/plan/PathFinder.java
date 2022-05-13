package hu.kristall.rpg.world.path.plan;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.path.Path;

public interface PathFinder {
	
	Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos);
	
}
