package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.FloatingItem;
import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.ArrayList;
import java.util.Collection;

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
				Collection<FloatingItem> floatingItems = h.getWorld().getItems();
				Collection<FloatingItem> targetItems = new ArrayList<>(floatingItems.size());
				Position playerPosition = h.getPosition();
				for (FloatingItem floatingItem : floatingItems) {
					Position itemPosition = floatingItem.getPosition();
					if(Position.distance(itemPosition, playerPosition) < 0.5) {
						targetItems.add(floatingItem);
					}
				}
				for (FloatingItem targetItem : targetItems) {
					h.getInventory().addItem(targetItem.getItem(), 1);
				}
				for (FloatingItem targetItem : targetItems) {
					targetItem.remove();
				}
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//network server won't accept packets from clients when the server is already shut down
			e.printStackTrace();
		}
	}
	
}
