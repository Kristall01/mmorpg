package hu.kristall.rpg.network;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.sync.Synchronizer;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkServer {
	
	private final Map<WsContext, NetworkConnection> connections = new ConcurrentHashMap<>();
	private final Synchronizer<Server> asyncServer;
	private Javalin javalinServer;
	private boolean wsAdded = false;
	private AtomicBoolean stopping = new AtomicBoolean(false);
	
	public NetworkServer(Server server, String servePath) {
		this.asyncServer = server.getSynchronizer();
		Javalin httpServer = Javalin.create(c -> {
			c.showJavalinBanner = false;
			if(servePath != null) {
				c.addStaticFiles(servePath, Location.EXTERNAL);
			}
		});
		this.javalinServer = httpServer;
		httpServer.start(8080);
	}
	
	//------------- ASYNC METHODS //-------------
	
	private void handleConnectionMessage(WsMessageContext ctx) {
		connections.get(ctx).handleNetworkMessage(ctx.message());
	}
	
	private void handleConnectionClose(WsCloseContext ctx) {
		connections.remove(ctx).handleConnectionClose();
	}
	
	private void handleConnect(WsConnectContext ctx) {
		if(stopping.get()) {
			ctx.closeSession();
			return;
		}
		connections.put(ctx, new WebsocketPlayerConnection(this.asyncServer, ctx));
	}
	
	private void handleConnectionError(WsErrorContext wsErrorContext) {
		wsErrorContext.session.close();
	}
	
	public void stop() {
		stopping.set(true);
		for (NetworkConnection value : connections.values()) {
			value.close("server stopping");
		}
		javalinServer.stop();
	}
	
	public void startAcceptingConnections() {
		if(wsAdded) {
			throw new IllegalStateException("ws is already added");
		}
		this.wsAdded =  true;
		javalinServer.ws("/ws", ws -> {
			ws.onConnect(this::handleConnect);
			ws.onMessage(this::handleConnectionMessage);
			ws.onError(this::handleConnectionError);
			ws.onClose(this::handleConnectionClose);
		});
	}
	
}
