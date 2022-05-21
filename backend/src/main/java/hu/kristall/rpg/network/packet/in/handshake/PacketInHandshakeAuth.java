package hu.kristall.rpg.network.packet.in.handshake;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.network.WebsocketPlayerConnection;
import hu.kristall.rpg.sync.AsyncExecutor;
import hu.kristall.rpg.sync.Synchronizer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PacketInHandshakeAuth extends PacketInHandshake {
	
	String name;
	
	@Override
	public void execute() {
		WebsocketPlayerConnection conn = getSender();
		try {
			conn.getAsyncServer().sync(srv -> {
				try {
					Future<Player> futurePlayer = srv.createPlayer(conn, name, true);
					AsyncExecutor.instance().runTask(() -> {
						try {
							futurePlayer.get();
						}
						catch (InterruptedException | ExecutionException e) {
							conn.close("Hiba történt a csatlakozás során.");
							e.printStackTrace();
						}
					});
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
