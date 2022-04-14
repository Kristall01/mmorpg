package hu.kristall.rpg.persistence;

import hu.kristall.rpg.Position;

public class LogoutPosition {
	
	public final Position pos;
	public final String worldName;
	
	public LogoutPosition(Position pos, String worldname) {
		this.pos = pos;
		this.worldName = worldname;
	}
	
}
