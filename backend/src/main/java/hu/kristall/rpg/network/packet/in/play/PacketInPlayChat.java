package hu.kristall.rpg.network.packet.in.play;

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
			this.getSender().getAsyncServer().sync(srv -> {
				srv.getCommandMap().executeCommand(getSender().getPlayer(), commandMsg);
			});
		}
		else {
			getSender().getPlayer().getAsyncEntity().sync(e -> {
				if(e == null) {
					return;
				}
				e.getWorld().broadcastMessage("§a"+e.getAsyncPlayer().name+" §7»§r "+message);
			});
		}
	}

}
