package hu.kristall.rpg.world.navmesh;

import hu.kristall.rpg.Position;

public class FunctionVector implements Vector {
	
	double centerY, xDiff, yDiff;
	
	public FunctionVector(Position from, Position to) {
		double yDiff = to.getY() - from.getY();
		centerY = from.getY() - from.getX() *yDiff;
	}
	
	@Override
	public boolean isIn(Position pos) {
		return pos.getX();
	}
	
}
