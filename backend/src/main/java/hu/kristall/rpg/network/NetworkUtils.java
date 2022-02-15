package hu.kristall.rpg.network;

import com.google.gson.JsonObject;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.network.packet.out.PacketOut;
import io.javalin.websocket.WsContext;

public class NetworkUtils {
	
	public static void sendJsonPacket(PacketOut packet, WsContext ctx) {
		JsonObject ob = new JsonObject();
		ob.addProperty("type", packet.type());
		ob.add("data", Utils.gson().toJsonTree(packet.serializedData()));
		ctx.send(Utils.toJson(ob));
	}
	
}
