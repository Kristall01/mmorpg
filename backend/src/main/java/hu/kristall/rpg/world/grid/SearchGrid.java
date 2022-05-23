package hu.kristall.rpg.world.grid;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.lineofsight.RayCaster;
import hu.kristall.rpg.world.path.Path;

import java.util.*;

public class SearchGrid {
	
	private SearchNode[] nodes;
	public final int width, height;
	
	private Position min, max;
	
	public SearchGrid(boolean[][] walls, int width, int height) {
		this.width = width;
		this.height = height;
		min = new Position(0,0);
		max = new Position(width,height);
		
		nodes = new SearchNode[height*width];
		for(int i = 0; i < nodes.length; ++i) {
			int x = i%width;
			int y = i/width;
			nodes[i] = new SearchNode(new GridPosition(x, y), walls[y][x]);
		}
	}
	
	private SearchNode getNode(GridPosition pos) {
		return nodes[pos.y*width + pos.x];
	}
	
	public GridPosition findClosestValidPoint(GridPosition pos) {
		if(!getNode(pos).isWall()) {
			return pos;
		}
		double minDist = Double.MAX_VALUE;
		GridPosition minGridPos = null;
		for(int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				GridPosition loopPosition = new GridPosition(x,y);
				SearchNode loopNode = getNode(loopPosition);
				if(loopNode.isWall()) {
					continue;
				}
				double dist = GridPosition.distance(pos, loopPosition);
				if(dist < minDist) {
					minDist = dist;
					minGridPos = loopPosition;
				}
			}
		}
		return minGridPos;
	}
	
	public List<Position> search(Position start, Position to) {
		GridPosition gridStart = findClosestValidPoint(Path.fixPosition(min, start, max).toGridPosition());
		GridPosition gridTo = findClosestValidPoint(Path.fixPosition(min, to, max).toGridPosition());
		Collection<GridPosition> gridPositions = search(gridStart, gridTo);
		if(gridPositions == null) {
			return null;
		}
		Position[] p = new Position[gridPositions.size()];
		int i = -1;
		for (GridPosition gridPosition : gridPositions) {
			p[++i] = gridPosition.toPosition();
		}
		return List.of(p);
	}
	
	private boolean runInner(Queue<SearchNode> openList, int x, int y, SearchNode node, GridPosition nodePos, GridPosition target) {
		if(x == nodePos.x && y == nodePos.y) {
			//center cell
			return false;
		}
		SearchNode childNode = getNode(new GridPosition(x, y));
		if(childNode.isClosed() || childNode.isWall()) {
			return false;
		}
		double cost = node.g + GridPosition.distance(nodePos, childNode.getPos());
		if(cost < childNode.g) {
			childNode.setParent(node);
			childNode.g = cost;
			childNode.updateF();
			if(childNode.isOpen()) {
				openList.remove(childNode);
				childNode.setOpen(false);
			} else if(childNode.isClosed()) {
				childNode.setClosed(true);
			}
		} else if(!childNode.isOpen() && !childNode.isClosed()) {
			childNode.setOpen(true);
			childNode.g = cost;
			childNode.setH(target);
			childNode.updateF();
			childNode.setParent(node);
			openList.add(childNode);
		}
		return true;
	}
	
	private void wallChecker(GridPosition p) {
		if(getNode(p).isWall()) {
			throw new RuntimeException();
		}
	}
	
	private List<GridPosition> search(GridPosition start, GridPosition target) {
		if(start.equals(target)) {
			return List.of(start, target);
		}
		
		for (SearchNode node : nodes) {
			node.reset();
		}
		Queue<SearchNode> openList = new PriorityQueue<>();
		SearchNode node = getNode(start);
		node.setH(target);
		node.updateF();
		node.setOpen(true);
		openList.add(node);
		
		while (!openList.isEmpty()) {
			node = openList.poll();
			node.setOpen(false);
			if(node.getPos().equals(target)) {
				LinkedList<GridPosition> positionChain = new LinkedList<>();
				SearchNode reverseLink = node.getParent();
				while (reverseLink != null) {
					positionChain.addFirst(reverseLink.getPos());
					reverseLink = reverseLink.getParent();
				}
				positionChain.add(target);
				LinkedList<Integer> removeCandidates = new LinkedList<>();
				int fromIndex = 0;
				for(int i = 1; i < positionChain.size()-1; ++i) {
					if(RayCaster.hasLineOfSight(positionChain.get(fromIndex).toPosition().add(0.5, 0.5), positionChain.get(i+1).toPosition().add(0.5,0.5), this::wallChecker)) {
						removeCandidates.addFirst(i);
					}
					else {
						fromIndex = i;
					}
				}
				for (Integer candidate : removeCandidates) {
					positionChain.remove((int)candidate);
				}
				return positionChain;
			}
			node.setClosed(true);
			GridPosition nodePos = node.getPos();
			
			int nodeX = nodePos.x, nodeY = nodePos.y;
			
			boolean tlVisitable = true;
			boolean trVisitable = true;
			boolean blVisitable = true;
			boolean brVisitable = true;
			
			int widthEnd = width-1, heightEnd = height-1;
			
			if(nodeX != 0) {
				boolean result = runInner(openList, nodeX-1, nodeY, node, nodePos, target);
				tlVisitable = result;
				blVisitable = result;
			}
			else {
				tlVisitable = false;
				blVisitable = false;
			}
			if(nodeX != widthEnd) {
				boolean result = runInner(openList, nodeX+1, nodeY, node, nodePos, target);
				trVisitable = result;
				brVisitable = result;
			}
			else {
				trVisitable = false;
				brVisitable = false;
			}
			if(nodeY != 0) {
				boolean result = runInner(openList, nodeX, nodeY-1, node, nodePos, target);
				tlVisitable &= result;
				trVisitable &= result;
			}
			else {
				tlVisitable = false;
				trVisitable = false;
			}
			if(nodeY != heightEnd) {
				boolean result = runInner(openList, nodeX, nodeY+1, node, nodePos, target);
				blVisitable &= result;
				brVisitable &= result;
			}
			else {
				blVisitable = false;
				brVisitable = false;
			}
			
			
			
			
			if(nodeX != 0) {
				if(tlVisitable) {
					runInner(openList, nodeX-1, nodeY-1, node, nodePos, target);
				}
				if(blVisitable) {
					runInner(openList, nodeX-1, nodeY+1, node, nodePos, target);
				}
			}
			if(nodeX != widthEnd) {
				if(trVisitable) {
					runInner(openList, nodeX+1, nodeY-1, node, nodePos, target);
				}
				if(brVisitable) {
					runInner(openList, nodeX+1, nodeY+1, node, nodePos, target);
				}
			}
			
			/*for (int x = Math.max(0, nodePos.x - 1); x < Math.min(width, nodePos.x + 2); ++x) {
				for (int y = Math.max(0, nodePos.y - 1); y < Math.min(height, nodePos.y + 2); ++y) {
					runInner(openList, x, y, node, nodePos, target);
				}
			}*/
		}
		//no path
		return null;
	}
		
}
