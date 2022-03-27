package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.sync.Synchronizer;

public class PacketInPlayChat extends PacketInPlay {
	
	private String message;
	
	@Override
	public void execute() {
		if(message.isEmpty()) {
			return;
		}
		if(message.charAt(0) == '/') {
			// :/
			String commandMsg = message.substring(1);
			try {
				this.getSender().getAsyncServer().sync(srv -> {
					srv.getCommandMap().executeCommand(getSender().getPlayer(), commandMsg);
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//network server won't accept packets from clients when the server is already shut down
				e.printStackTrace();
			}
		}
		else {
			try {
				getSender().getPlayer().getAsyncEntity().sync(e -> {
					if(e == null) {
						getSender().sendPacket(new PacketOutChat("§cHiba: §4Nem vagy része egy világnak sem, ahova el lehetne küldeni az üzenetetet."));
						return;
					}
					e.getWorld().broadcastMessage("§a"+e.getAsyncPlayer().name+" §7»§r "+message);
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//network server won't accept packets from clients when the server is already shut down
				e.printStackTrace();
			}
		}
	}

}
