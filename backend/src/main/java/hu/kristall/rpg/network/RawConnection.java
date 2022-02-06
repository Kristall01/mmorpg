package hu.kristall.rpg.network;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.Synchronizer;
import hu.kristall.rpg.network.phase.Phase;
import hu.kristall.rpg.network.phase.PhaseAuth;
import io.javalin.websocket.WsContext;

public class RawConnection {
	
	public Phase phase;
	public final WsContext context;
	//public final NetworkServer networkServer;
	public Synchronizer<Server> asyncServer;
	
	public RawConnection(Synchronizer<Server> asyncServer, WsContext context) {
		this.context = context;
		this.phase = new PhaseAuth(this);
		//this.networkServer = networkServer;
		this.asyncServer = asyncServer;
	}
	
}
