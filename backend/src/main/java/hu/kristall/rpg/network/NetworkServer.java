package hu.kristall.rpg.network;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.network.config.HostConfigurator;
import hu.kristall.rpg.sync.AsyncExecutor;
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
	
	public NetworkServer(Server server, int port, HostConfigurator hostConfigurator) {
		this.asyncServer = server.getSynchronizer();
		Javalin httpServer = Javalin.create(c -> {
			c.showJavalinBanner = false;
			if(hostConfigurator != null) {
				c.addStaticFiles(hostConfigurator);
			}
		});
		this.javalinServer = httpServer;
		httpServer.start(port);
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
	
	public void stop(Runnable afterStop) {
		stopping.set(true);
		for (NetworkConnection value : connections.values()) {
			value.close("server stopping");
		}
		AsyncExecutor.instance().runTask(() -> {
			javalinServer.stop();
			try {
				asyncServer.sync(srv -> {
					afterStop.run();
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//lol
				e.printStackTrace();
			}
		});
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
