package hu.kristall.rpg.world.path;

import hu.kristall.rpg.Position;

public interface PositionFunction {

	Position getCurrentPosition();
	
	
	boolean moving();

}
