package hu.kristall.rpg.network;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.network.packet.out.PacketOut;

public interface PlayerConnection {
	
	Player getPlayer();
	void close(String reason);
	void sendPacket(PacketOut packet);
	void joinGame(Player player);
	
}
