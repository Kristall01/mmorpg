package hu.kristall.rpg.network;

import com.google.gson.JsonObject;
import hu.kristall.rpg.Player;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.network.packet.out.PacketOut;
import io.javalin.websocket.WsContext;

public class PlayerConnection {
	
	private final WsContext context;
	private Player player;
	
	public PlayerConnection(WsContext context) {
		this.context = context;
	}
	
	public void sendPacket(PacketOut out) {
		try {
			JsonObject ob = new JsonObject();
			ob.addProperty("type", out.type());
			ob.add("data", Utils.gson().toJsonTree(out.serializedData()));
			context.send(Utils.toJson(ob));
		}
		catch (Exception ex) {
			//TODO handler packet send error
		}
	}
	
	public void close() {
		context.session.close();
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
