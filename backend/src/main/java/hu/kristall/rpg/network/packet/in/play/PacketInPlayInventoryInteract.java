package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.inventory.Inventory;

public class PacketInPlayInventoryInteract extends PacketInPlay {
	
	String type;
	String inventoryID;
	
	@Override
	public void execute() {
		try {
			getSender().getPlayer().getAsyncEntity().sync(wp -> {
				if(wp == null || !wp.hasEntity()) {
					return;
				}
				EntityHuman e = wp.getEntity();
				if(inventoryID.equals("default")) {
					e.getInventory().interactItem(e,type);
					return;
				}
				Inventory inv = e.getWorld().getInventory(inventoryID);
				if(inv == null) {
					return;
				}
				inv.interactItem(e, type);
			});
		}
		catch (Synchronizer.TaskRejectedException ex) {
			//network server won't accept packets from clients when the server is already shut down
			ex.printStackTrace();
		}
	}
	
}
