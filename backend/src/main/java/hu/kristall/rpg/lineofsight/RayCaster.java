package hu.kristall.rpg.lineofsight;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.grid.GridPosition;

import java.util.function.Consumer;

public class RayCaster {
	
	private static final double precision = Math.pow(2,-20);
	
	private static boolean doubleEquals(double a, double b) {
		return Math.abs(a-b) < precision;
	}
	
	private static void checkAround(Consumer<GridPosition> blocked, int x, int y) {
		blocked.accept(new GridPosition(x,y));
		blocked.accept(new GridPosition(x-1,y));
		blocked.accept(new GridPosition(x,y-1));
		blocked.accept(new GridPosition(x-1,y-1));
	}
	
	private static void checkUpDown(Consumer<GridPosition> blocked, int x, int y) {
		blocked.accept(new GridPosition(x,y));
		blocked.accept(new GridPosition(x,y-1));
	}
	
	private static void checkLeftRight(Consumer<GridPosition> blocked, int x, int y) {
		blocked.accept(new GridPosition(x,y));
		blocked.accept(new GridPosition(x-1,y));
	}
	
	private static void iterateXStep(double currentY, Consumer<GridPosition> blocked, int x) {
		int roundedY = (int)Math.floor(currentY);
		if(doubleEquals(Math.abs(roundedY - currentY), 0)) {
			checkAround(blocked, x, roundedY);
		}
		else {
			//no need for checking twice
			checkLeftRight(blocked, x, roundedY);
		}
	}
	
	private static void iterateYStep(double currentX, Consumer<GridPosition> blocked, int y) {
		int roundedX = (int) Math.floor(currentX);
		if(doubleEquals(Math.abs(roundedX - currentX), 0)) {
			checkAround(blocked, roundedX, y);
		} else {
			//no need for checking twice
			checkUpDown(blocked, roundedX, y);
		}
	}
	
	public static boolean hasLineOfSight(Position fromPosition, Position toPosition, Consumer<GridPosition> blocked) {
		try {
			double
				fromX = fromPosition.getX(),
				toX = toPosition.getX(),
				fromY = fromPosition.getY(),
				toY = toPosition.getY();
			
			double
				xDiff = toX - fromX,
				yDiff = toY - fromY;
			
			
			GridPosition fromPositionGrid = fromPosition.toGridPosition();
			GridPosition toPositionGrid = toPosition.toGridPosition();
			
			if(fromPositionGrid.equals(toPositionGrid)) {
				blocked.accept(fromPositionGrid);
				return true;
			}
			int
				fromXgrid = fromPositionGrid.x,
				toXgrid = toPositionGrid.x,
				fromYgrid = fromPositionGrid.y,
				toYgrid = toPositionGrid.y;
			
			
			if(fromYgrid == toYgrid) {
				int direction = (int) (Math.signum(toXgrid - fromXgrid));
				int x = fromXgrid;
				for (; x != toXgrid; x += direction) {
					blocked.accept(new GridPosition(x, fromYgrid));
				}
				blocked.accept(new GridPosition(x, fromYgrid));
				return true;
			}
			if(fromXgrid == toXgrid) {
				int direction = (int) (Math.signum(toYgrid - fromYgrid));
				int y = fromYgrid;
				for (; y != toYgrid; y += direction) {
					blocked.accept(new GridPosition(fromXgrid, y));
				}
				blocked.accept(new GridPosition(fromXgrid, y));
				return true;
			}
			
			//CHECK VERTICAL
			GridPosition
				startCell,
				endCell;
			blocked.accept(fromPositionGrid);
			//iterate over X
			{
				int xIteratorDiff = 0 < xDiff ? 1 : -1;
				int loopX = 0 < xIteratorDiff ? (int)Math.ceil(fromX) : (int)Math.floor(fromX);
				double yRate = (1 / xDiff) * yDiff;
				double currentY = fromY + (loopX - fromX) * yRate;
				int target = 0 < xDiff ? (int)Math.floor(toX) : (int)Math.ceil(toX);
				for (; loopX != target; loopX += xIteratorDiff) {
					iterateXStep(currentY, blocked, loopX);
					currentY += yRate * xIteratorDiff;
				}
				iterateXStep(currentY, blocked, loopX);
			}
			//iterate over Y
			{
				int yIteratorDiff = 0 < yDiff ? 1 : -1;
				int loopY = 0 < yIteratorDiff ? (int)Math.ceil(fromY) : (int)Math.floor(fromY);
				double xRate = (1 / yDiff) * xDiff;
				double currentX = fromX + (loopY - fromY) * xRate;
				int target = 0 < yDiff ? (int)Math.floor(toY) : (int)Math.ceil(toY);
				for (; loopY != target; loopY += yIteratorDiff) {
					iterateYStep(currentX, blocked, loopY);
					currentX += xRate * yIteratorDiff;
				}
				iterateYStep(currentX, blocked, loopY);
			}
			return true;
		}
		catch (RuntimeException ex) {
			return false;
		}
	}
	
	/*public static void main(String[] args) {
		BlockDetector bd = new BlockDetector(List.of(
		));
		Position
			from = new Position(0.5, 1),
			to = new Position(2.7, 2.9);
		boolean hasSight = hasLineOfSight(from, to, bd);
		
		JsonArray arr = new JsonArray();
		for (GridPosition checkedCell : bd.checkedCells()) {
			arr.add(Utils.gson().toJsonTree(checkedCell));
		}
		JsonObject obj = new JsonObject();
		obj.add("sightpoints", arr);
		obj.add("from", Utils.gson().toJsonTree(from));
		obj.add("to", Utils.gson().toJsonTree(to));
		
		double left = Math.min(from.getX(), to.getX());
		double right = Math.max(from.getX(), to.getX());
		for (GridPosition checkedCell : bd.checkedCells()) {
			left = Math.min(left, checkedCell.x);
			right = Math.max(right, checkedCell.x);
		}
		double bottom = Math.min(from.getY(), to.getY());
		double top = Math.max(from.getY(), to.getY());
		for (GridPosition checkedCell : bd.checkedCells()) {
			bottom = Math.min(bottom, checkedCell.y);
			top = Math.max(top, checkedCell.y);
		}
		
		
		obj.addProperty("left", left-1);
		obj.addProperty("top", top + 1);
		obj.addProperty("right", right + 1);
		obj.addProperty("bottom", bottom - 1);
		obj.addProperty("result", hasSight);
		
		try {
			Files.writeString(new File("").toPath(), Utils.gson().toJson(obj));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}*/
	
}
