package hu.kristall.rpg.network.packet.in.handshake;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.network.WebsocketPlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOutAuthenticated;

public class PacketInHandshakeAuth extends PacketInHandshake {
	
	String name;
	
	@Override
	public void execute() {
		WebsocketPlayerConnection conn = getSender();
		conn.getAsyncServer().sync(srv -> {
			try {
				srv.createPlayer(conn, name);
				conn.sendPacket(new PacketOutAuthenticated());
			}
			catch (Server.PlayerNameAlreadyOnlineException e) {
				conn.close("Ez a név már foglalt.");
			}
		});
	}
	
}
