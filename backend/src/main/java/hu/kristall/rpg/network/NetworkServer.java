package hu.kristall.rpg.network;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.Synchronizer;
import io.javalin.Javalin;
import io.javalin.websocket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkServer {
	
	private final Map<WsContext, RawConnection> connections = new ConcurrentHashMap<>();
	private final Synchronizer<Server> asyncServer;
	
	public NetworkServer(Server server) {
		//this.server = server;
		this.asyncServer = server.getSynchronizer();
		
		//packetRegistry = PacketRegistry.baseRegistry();
		
		Javalin httpServer = Javalin.create(c -> {
			c.showJavalinBanner = false;
			//c.addStaticFiles("/hdd/teams_records/szakdolgozat/game/build", Location.EXTERNAL);
		});
		httpServer.ws("/ws", ws -> {
			ws.onConnect(this::handleConnect);
			ws.onMessage(this::handleConnectionMessage);
			ws.onError(this::handleConnectionError);
			ws.onClose(this::handleConnectionClose);
		});
		httpServer.start(8080);
	}
	
	//------------- ASYNC METHODS //-------------
	
	private void handleConnectionMessage(WsMessageContext ctx) {
		connections.get(ctx).phase.processMessage(ctx.message());
	}
	
	private void handleConnectionClose(WsCloseContext ctx) {
		connections.remove(ctx).phase.handleDisconnect();
		//TODO remove connection from game logic
		/*PlayerConnection conn = connections.remove(ctx);
		asyncServer.sync((srv) -> {
			srv.getPlayerManager().removePlayer(conn);
		});*/
	}
	
	private void handleConnect(WsConnectContext ctx) {
		connections.put(ctx, new RawConnection(this.asyncServer, ctx));
	}
	
	private void handleConnectionError(WsErrorContext wsErrorContext) {
		wsErrorContext.session.close();
	}
	
}
