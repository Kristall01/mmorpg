package hu.kristall.rpg.network.phase;

import com.google.gson.JsonObject;
import hu.kristall.rpg.Player;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.network.PacketMap;
import hu.kristall.rpg.network.RawConnection;
import hu.kristall.rpg.network.packet.in.PacketIn;
import hu.kristall.rpg.network.packet.in.play.PacketInPlay;
import hu.kristall.rpg.network.packet.in.play.PacketInPlayChat;
import hu.kristall.rpg.network.packet.in.play.PacketInPlayMove;
import hu.kristall.rpg.network.packet.in.play.PacketInPlayPing;
import hu.kristall.rpg.network.packet.out.PacketOut;

public class PhasePlay implements Phase {
	
	private final RawConnection rawConnection;
	private final PacketMap<PacketInPlay> packetMap = basicPlayPacketMap;
	private Player player;
	
	public PhasePlay(RawConnection conn, String username) {
		this.rawConnection = conn;
		this.player = new Player(this, username);
		conn.asyncServer.sync(srv -> {
			srv.getPlayerWorldManager().changeWorld(this.player,srv.getWorldsManager().getDefaultWorld());
		});
	}
	
	@Override
	public void processMessage(String message) {
		//handled on network thread
		
		int index = message.indexOf(';');
		if(index == -1) {
			rawConnection.context.session.close();
			//conn.kick("bad backet type: "+packetType);
			return;
		}
		String packetType = message.substring(0, index);
		String packetContext = message.substring(index+1);
		
		Class<? extends PacketIn> packetClass = packetMap.get(packetType);
		if(packetClass == null) {
			rawConnection.context.session.close();
			//conn.kick("bad backet type: "+packetType);
			return;
		}
		PacketInPlay packet = null;
		try {
			packet = Utils.gson().<PacketInPlay>fromJson(packetContext, packetClass);
			packet.sender = player;
		}
		catch(Exception e) {
			System.out.println("failed to serialize packet");
			e.printStackTrace();
			rawConnection.context.session.close();
			return;
		}
		packet.execute();
		//rawConnection.asyncServer.sync(packet::execute).get();
	}
	
	
	//called from server thread
	@Override
	public void handleDisconnect() {
		rawConnection.asyncServer.sync(srv -> {
			srv.getPlayerWorldManager().changeWorld(player, null);
		});
	}
	
	//callable from all thread
	public void sendPacket(PacketOut out) {
		try {
			JsonObject ob = new JsonObject();
			ob.addProperty("type", out.type());
			ob.add("data", Utils.gson().toJsonTree(out.serializedData()));
			rawConnection.context.send(Utils.toJson(ob));
		}
		catch (Exception ex) {
			//TODO handler packet send error
		}
	}
	
	// ----------------------------- static stuff ----------------------------- //
	
	
	
	private static final PacketMap<PacketInPlay> basicPlayPacketMap;
	
	static {
		PacketMap.Builder<PacketInPlay> packetMapBuilder = new PacketMap.Builder<PacketInPlay>();
		packetMapBuilder.register("chat", PacketInPlayChat.class);
		packetMapBuilder.register("ping", PacketInPlayPing.class);
		packetMapBuilder.register("move", PacketInPlayMove.class);
		basicPlayPacketMap = packetMapBuilder.build();
	}
	
}
