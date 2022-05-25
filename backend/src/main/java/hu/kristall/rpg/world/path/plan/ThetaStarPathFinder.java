package hu.kristall.rpg.world.path.plan;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.lineofsight.RayCaster;
import hu.kristall.rpg.world.grid.GridPosition;
import hu.kristall.rpg.world.grid.SearchGrid;
import hu.kristall.rpg.world.path.Path;
import hu.kristall.rpg.world.path.ZigzagPosition;

import java.util.LinkedList;
import java.util.List;

public class ThetaStarPathFinder implements PathFinder {
	
	private SearchGrid grid;
	
	public ThetaStarPathFinder(SearchGrid grid) {
		this.grid = grid;
	}
	
	private void wallChecker(GridPosition p) {
		if(grid.isWall(p)) {
			throw new RuntimeException();
		}
	}
	
	@Override
	public Path findPath(Position from, Position to, double cellsPerSec, long startTimeNanos) {
		List<Position> positions = grid.search(from, to);
		if(positions == null) {
			return null;
		}
		positions = new LinkedList<>(positions);
		
		LinkedList<Integer> removeCandidates = new LinkedList<>();
		int fromIndex = 0;
		for(int i = 1; i < positions.size()-1; ++i) {
			if(RayCaster.hasLineOfSight(positions.get(fromIndex), positions.get(i+1), this::wallChecker)) {
				removeCandidates.addFirst(i);
			}
			else {
				fromIndex = i;
			}
		}
		for (Integer candidate : removeCandidates) {
			positions.remove((int)candidate);
		}
		
		return new Path(positions.get(positions.size()-1), positions, new ZigzagPosition(positions, startTimeNanos, cellsPerSec), startTimeNanos);
	}
}
