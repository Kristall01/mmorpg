package hu.kristall.rpg.lineofsight;

import hu.kristall.rpg.world.grid.GridPosition;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BlockDetector implements Consumer<GridPosition> {
	
	private List<GridPosition> pos;
	private Set<GridPosition> checkedPositions = new HashSet<>();
	
	public BlockDetector(List<GridPosition> walls) {
		this.pos = walls;
	}
	
	@Override
	public void accept(GridPosition gridPosition) {
		if(pos.contains(gridPosition)) {
			throw new RuntimeException();
		}
		checkedPositions.add(gridPosition);
	}
	
	public Set<GridPosition> checkedCells() {
		return Collections.unmodifiableSet(checkedPositions);
	}
	
}
