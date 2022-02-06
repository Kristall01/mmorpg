package hu.kristall.rpg.world.path;

import hu.kristall.rpg.Position;

public class ConstantPosition implements PositionFunction {
	
	private Position position;
	
	public ConstantPosition(Position position) {
		this.position = position;
	}
	
	@Override
	public Position getCurrentLocation() {
		return position;
	}
	
}
