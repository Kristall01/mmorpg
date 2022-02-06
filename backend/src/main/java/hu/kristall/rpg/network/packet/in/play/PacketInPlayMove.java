package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.Synchronizer;
import hu.kristall.rpg.world.entity.EntityPlayer;

public class PacketInPlayMove extends PacketInPlay {
	
	private double x;
	private double y;
	
	@Override
	public void execute() {
		Synchronizer<EntityPlayer> p = sender.getAsyncEntity();
		if(p == null) {
			return;
		}
		p.sync(e -> {
			if(e == null) {
				return;
			}
			
			e.move(new Position(x,y));
		});
	}
	
}
