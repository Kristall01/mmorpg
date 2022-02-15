package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.world.entity.EntityHuman;

public class PacketInPlayMove extends PacketInPlay {
	
	private double x;
	private double y;
	
	@Override
	public void execute() {
		getSender().getPlayer().getAsyncEntity().sync(e -> {
			if(e == null) {
				//player left world
				return;
			}
			EntityHuman h = e.getEntity();
			if(h == null) {
				//player is dead, went shaco ult, or something
				return;
			}
			h.move(new Position(x,y));
		});
	}
	
}
