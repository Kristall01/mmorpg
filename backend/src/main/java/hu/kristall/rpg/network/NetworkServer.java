package hu.kristall.rpg.network;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.sync.Synchronizer;
import io.javalin.Javalin;
import io.javalin.websocket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkServer {
	
	private final Map<WsContext, NetworkConnection> connections = new ConcurrentHashMap<>();
	//private final Synchronizer<Server> asyncServer;
	private Javalin javalinServer;
	private AtomicBoolean stopping = new AtomicBoolean(false);
	private Map<String, Synchronizer<Server>> servers = new ConcurrentHashMap<>();
	
	public NetworkServer() {
		//this.asyncServer = server.getSynchronizer();
		Javalin httpServer = Javalin.create(c -> {
			c.showJavalinBanner = false;
			//c.addStaticFiles("/hdd/teams_records/szakdolgozat/game/build", Location.EXTERNAL);
		});
		this.javalinServer = httpServer;
		javalinServer.ws("/{server}", ws -> {
			ws.onConnect(this::handleConnect);
			ws.onMessage(this::handleConnectionMessage);
			ws.onError(this::handleConnectionError);
			ws.onClose(this::handleConnectionClose);
		});
		httpServer.start(8080);
	}
	
	//------------- ASYNC METHODS //-------------
	
	private void handleConnectionMessage(WsMessageContext ctx) {
		connections.get(ctx).handleNetworkMessage(ctx.message());
	}
	
	private void handleConnectionClose(WsCloseContext ctx) {
		NetworkConnection conn = connections.remove(ctx);
		if(conn != null) {
			conn.handleConnectionClose();
		}
	}
	
	public void addServer(String ID, Synchronizer<Server> server) {
		server.sync(srv -> {
			if(srv == null) {
				return;
			}
			srv.addShutdownListener(shutdown -> servers.remove(ID));
			this.servers.put(ID, server);
		});
	}
	
	private void handleConnect(WsConnectContext ctx) {
		if(stopping.get()) {
			ctx.closeSession();
			return;
		}
		Synchronizer<Server> srv = servers.get(ctx.pathParam("server"));
		if(srv == null) {
			ctx.closeSession();
			return;
		}
		connections.put(ctx, new WebsocketPlayerConnection(srv, ctx));
	}
	
	private void handleConnectionError(WsErrorContext wsErrorContext) {
		wsErrorContext.session.close();
	}
	
	public void stop() {
		if(stopping.get()) {
			return;
		}
		stopping.set(true);
		for (NetworkConnection value : connections.values()) {
			value.close("server stopping");
		}
		javalinServer.stop();
	}
	
}
