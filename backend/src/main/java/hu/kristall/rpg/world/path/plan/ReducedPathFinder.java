package hu.kristall.rpg.world.path.plan;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.path.LinearPosition;
import hu.kristall.rpg.world.path.Path;

import java.util.List;

import static hu.kristall.rpg.world.path.Path.fixPosition;

public class ReducedPathFinder implements PathFinder {
	

	private Position min, max;
	
	public ReducedPathFinder(Position min, Position max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		from = fixPosition(min, from, max);
		to = fixPosition(min, to, max);
		return new Path(to, List.of(from, to), new LinearPosition(from, to, cellsPerSec, startTimeNanos), startTimeNanos);
	}
	
}
