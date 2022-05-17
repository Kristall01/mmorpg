package hu.kristall.rpg.world.navmesh;

import hu.kristall.rpg.Position;

public class PositiveVerticalVector implements Vector {
	
	private double x;
	
	public PositiveVerticalVector(double x) {
		this.x = x;
	}
	
	@Override
	public boolean isIn(Position pos) {
		return x <= pos.getX();
	}
	
}
