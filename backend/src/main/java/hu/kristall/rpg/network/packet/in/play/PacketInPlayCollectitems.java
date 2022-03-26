package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;

public class PacketInPlayCollectitems extends PacketInPlay {
	
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
				e.getEntity().pickupNearbyItems(0.5);
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//network server won't accept packets from clients when the server is already shut down
			e.printStackTrace();
		}
	}
	
}
