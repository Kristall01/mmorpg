package hu.kristall.rpg.network;

import hu.kristall.rpg.AsyncServer;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.sync.AsyncExecutor;
import hu.kristall.rpg.sync.Synchronizer;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkServer {
	
	private final Map<WsContext, NetworkConnection> connections = new ConcurrentHashMap<>();
	private Synchronizer<Server> asyncServer;
	private final Javalin javalinServer;
	private boolean stopping = false;
	private final Object stopLock = new Object();
	private Logger logger = LoggerFactory.getLogger("NetworkServer");
	
	public NetworkServer(String servePath) {
		Javalin httpServer = Javalin.create(c -> {
			c.showJavalinBanner = false;
			if(servePath != null) {
				c.addStaticFiles(servePath, Location.EXTERNAL);
			}
		});
		this.javalinServer = httpServer;
		try {
			httpServer.start(8080);
		}
		catch (Exception ex) {
			logger.error("failed to bind port");
			throw ex;
		}
	}
	
	//------------- ASYNC METHODS //-------------
	
	private void handleConnectionMessage(WsMessageContext ctx) {
		connections.get(ctx).handleNetworkMessage(ctx.message());
	}
	
	private void handleConnectionClose(WsCloseContext ctx) {
		connections.remove(ctx).handleConnectionClose();
	}
	
	private void handleConnect(WsConnectContext ctx) {
		synchronized(stopLock) {
			if(stopping || asyncServer == null) {
				ctx.closeSession();
				return;
			}
			connections.put(ctx, new WebsocketPlayerConnection(this.asyncServer, ctx));
		}
	}
	
	private void handleServerShutdown() {
	
	}
	
	private void handleConnectionError(WsErrorContext wsErrorContext) {
		wsErrorContext.session.close();
	}
	
	public synchronized void stop() {
		synchronized(stopLock) {
			if(stopping) {
				return;
			}
			stopping = true;
			for (NetworkConnection value : connections.values()) {
				value.close("server stopping");
			}
		}

		AsyncExecutor.instance().runTask(() -> {
			javalinServer.stop();
			/*try {
				asyncServer.sync(srv -> {
					afterStop.run();
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//lol
				e.printStackTrace();
			}*/
		});
	}
	
	private final Object addServerLock = new Object();
	
	public synchronized void addServer(AsyncServer server) {
		synchronized(addServerLock) {
			if(asyncServer != null) {
				throw new IllegalStateException("server is already added");
			}
			asyncServer = server;
			javalinServer.ws("/ws", ws -> {
				ws.onConnect(this::handleConnect);
				ws.onMessage(this::handleConnectionMessage);
				ws.onError(this::handleConnectionError);
				ws.onClose(this::handleConnectionClose);
			});
		}
	}
	
}
