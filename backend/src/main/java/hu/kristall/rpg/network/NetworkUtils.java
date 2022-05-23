package hu.kristall.rpg.network;

import hu.kristall.rpg.Utils;
import hu.kristall.rpg.network.packet.out.PacketOut;
import io.javalin.websocket.WsContext;

public class NetworkUtils {
	
	public static void sendJsonPacket(PacketOut packet, WsContext ctx) {
		String packetType = packet.type();
		StringBuilder sb = new StringBuilder(packetType.length()+1);
		sb.append(packetType);
		sb.append(';');
		Utils.gson().toJson(packet.serializedData(), sb);
		ctx.send(sb.toString());
	}
	
}
