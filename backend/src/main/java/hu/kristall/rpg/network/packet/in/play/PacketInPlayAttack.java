package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;

public class PacketInPlayAttack extends PacketInPlay {
	
	double x,y;
	
	
	@Override
	public void execute() {
		try {
			getSender().getPlayer().getAsyncEntity().sync(e -> {
				if(e == null) {
					//player left world
					return;
				}
				EntityHuman h = e.getEntity();
				if(h == null) {
					//player is dead, went shaco ult, or something like that
					return;
				}
				h.attackTowards(new Position(x, y));
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//network server won't accept packets from clients when the server is already shut down
			e.printStackTrace();
		}
		
	}
	
}
