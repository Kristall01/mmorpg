package hu.kristall.rpg.world.grid;

import org.jetbrains.annotations.NotNull;

public class SearchNode implements Comparable<SearchNode> {
	
	private boolean closed = false;
	private boolean open = false;
	private final GridPosition pos;
	private final boolean wall;
	private SearchNode parent;
	
	public SearchNode(GridPosition pos, boolean wall) {
		this.pos = pos;
		this.wall = wall;
	}
	
	//combined dist
	public double g;

	//dist to start
	public double f;
	
	//dist to target cell
	public double h;
	
	public void setParent(SearchNode parent) {
		this.parent = parent;
	}
	
	public SearchNode getParent() {
		return parent;
	}
	
	public boolean isWall() {
		return wall;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void setH(GridPosition other) {
		this.h = GridPosition.distance(this.pos, other);
	}
	
	public void updateF() {
		this.f = this.h + this.g;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public void reset() {
		closed = false;
		open = false;
		f = 0;
		g = 0;
		h = 0;
		parent = null;
	}
	
	public GridPosition getPos() {
		return pos;
	}
	
	@Override
	public int compareTo(@NotNull SearchNode o) {
		return Double.compare(f, o.f);
	}
}
