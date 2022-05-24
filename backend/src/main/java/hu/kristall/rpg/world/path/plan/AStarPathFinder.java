package hu.kristall.rpg.world.path.plan;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.grid.SearchGrid;
import hu.kristall.rpg.world.path.Path;
import hu.kristall.rpg.world.path.ZigzagPosition;

import java.util.List;

public class AStarPathFinder implements PathFinder {
	
	private SearchGrid grid;
	
	public AStarPathFinder(SearchGrid grid) {
		this.grid = grid;
	}
	
	@Override
	public Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		List<Position> positions = grid.search(from, to);
		if(positions == null) {
			return null;
		}
		
		return new Path(positions.get(positions.size()-1), positions, new ZigzagPosition(positions, startTimeNanos, cellsPerSec), startTimeNanos);
	}
	
}
