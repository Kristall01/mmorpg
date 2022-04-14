package hu.kristall.rpg.network;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.network.packet.in.play.*;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.network.packet.in.PacketIn;
import hu.kristall.rpg.network.packet.in.handshake.PacketInHandshake;
import hu.kristall.rpg.network.packet.in.handshake.PacketInHandshakeAuth;
import hu.kristall.rpg.network.packet.out.PacketOut;
import hu.kristall.rpg.network.packet.out.PacketOutDisconnect;
import io.javalin.websocket.WsContext;

public class WebsocketPlayerConnection implements NetworkConnection, PlayerConnection {
	
	private final WsContext context;
	private Synchronizer<Server> asyncServer;
	private Player player;
	private PacketMap<?> packets;
	
	public WebsocketPlayerConnection(Synchronizer<Server> asyncServer, WsContext context) {
		this.packets = handshakePacketMap;
		this.context = context;
		this.asyncServer = asyncServer;
	}
	
	public boolean authenticated() {
		return player != null;
	}
	
	public Synchronizer<Server> getAsyncServer() {
		return asyncServer;
	}
	
	public void handleNetworkMessage(String message) {
		int index = message.indexOf(';');
		if(index == -1) {
			close("bad packet format");
			return;
		}
		String packetType = message.substring(0, index);
		String packetContext = message.substring(index+1);
		
		Class<? extends PacketIn> packetClass = packets.get(packetType);
		if(packetClass == null) {
			close("bad packet type '"+packetType+'\'');
			return;
		}
		PacketIn packet = null;
		try {
			packet = Utils.gson().<PacketIn>fromJson(packetContext, packetClass);
			packet.setSender(this);
		}
		catch(Exception e) {
			e.printStackTrace();
			close("packet deserialization error");
			return;
		}
		packet.execute();
	}
	
	//server thread
	public void joinGame(Player player) {
		this.player = player;
		this.packets = playPacketMap;
	}
	
	//called from network thread
	@Override
	public void handleConnectionClose() {
		if(!authenticated()) {
			return;
		}
		try {
			final Player player = getPlayer();
			getAsyncServer().sync(srv -> player.handleQuit());
		}
		catch (Synchronizer.TaskRejectedException e) {
			//server will be running while players are online
			e.printStackTrace();
		}
	}
	
	//any thread
	@Override
	public Player getPlayer() {
		return this.player;
	}
	
	//any thread
	@Override
	public void sendPacket(PacketOut packet) {
		NetworkUtils.sendJsonPacket(packet, context);
	}
	
	
	//any thread
	@Override
	public void close(String reason) {
		sendPacket(new PacketOutDisconnect(reason));
		context.session.close();
	}
	
	// ----------------------------- packet maps init ----------------------------- //
	
	private static final PacketMap<PacketInPlay> playPacketMap;
	private static final PacketMap<PacketInHandshake> handshakePacketMap;
	
	static {
		PacketMap.Builder<PacketInPlay> playPacketMapBuilder = new PacketMap.Builder<PacketInPlay>();
		playPacketMapBuilder.register("chat", PacketInPlayChat.class);
		playPacketMapBuilder.register("ping", PacketInPlayPing.class);
		playPacketMapBuilder.register("move", PacketInPlayMove.class);
		playPacketMapBuilder.register("collect-items", PacketInPlayCollectitems.class);
		playPacketMapBuilder.register("apply-clothes", PacketInPlayApplyClothes.class);
		playPacketMap = playPacketMapBuilder.build();
		
		PacketMap.Builder<PacketInHandshake> handshakePacketMapBuilder = new PacketMap.Builder<PacketInHandshake>();
		handshakePacketMapBuilder.register("auth", PacketInHandshakeAuth.class);
		handshakePacketMap = handshakePacketMapBuilder.build();
	}
	
}
