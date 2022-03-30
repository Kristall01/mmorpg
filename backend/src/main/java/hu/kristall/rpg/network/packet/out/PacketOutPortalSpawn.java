package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.Portal;

public class PacketOutPortalSpawn extends PacketOut {
	
	double X, Y, radius;
	
	private PacketOutPortalSpawn() {
		super("portal-spawn");
	}
	
	public PacketOutPortalSpawn(double x, double y, double radius) {
		this();
		this.X = x;
		this.Y = y;
		this.radius = radius;
	}
	
	public PacketOutPortalSpawn(Portal portal) {
		this();
		Position p = portal.getPosition();
		this.X = p.getX();
		this.Y = p.getY();
		this.radius = portal.getRadius();
	}
	
}
