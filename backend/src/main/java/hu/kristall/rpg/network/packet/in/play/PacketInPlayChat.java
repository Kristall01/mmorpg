package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.Synchronizer;
import hu.kristall.rpg.world.entity.EntityPlayer;

public class PacketInPlayChat extends PacketInPlay {
	
	private String message;
	
	@Override
	public void execute() {
		if(message.isEmpty()) {
			return;
		}
		if(message.charAt(0) == '/') {
			// :/
			//server.getCommandMap().executeCommand(, message.substring(1));
			sender.sendMessage("§cEgyelőre nincsenek parancsok. :/");
			return;
		}
		Synchronizer<EntityPlayer> p = sender.getAsyncEntity();
		if(p == null) {
			return;
		}
		p.sync(e -> {
			if(e == null) {
				return;
			}
			e.getWorld().broadcastMessage("§a"+e.getPlayer().getName()+" §7»§r "+message);
		});
		//server.broadcastMessage("§aplayername §7»§r "+message);*/
	}

}
