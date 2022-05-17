package hu.kristall.rpg.world.navmesh;

import hu.kristall.rpg.Position;

public class NegativeVerticalVector implements Vector {
	
	private double x;
	
	public NegativeVerticalVector(double x) {
		this.x = x;
	}
	
	@Override
	public boolean isIn(Position pos) {
		return pos.getX() <= x;
	}
}
