package hu.kristall.rpg.network.packet.in.handshake;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.network.WebsocketPlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOutAuthenticated;
import hu.kristall.rpg.sync.Synchronizer;

public class PacketInHandshakeAuth extends PacketInHandshake {
	
	String name;
	
	@Override
	public void execute() {
		WebsocketPlayerConnection conn = getSender();
		try {
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
		catch (Synchronizer.TaskRejectedException e) {
			//network server won't accept packets from clients when the server is already shut down
			e.printStackTrace();
		}
	}
	
}
