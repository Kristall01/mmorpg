package hu.kristall.rpg;

import hu.kristall.rpg.lang.Lang;
import hu.kristall.rpg.sync.Synchronizer;

public class AsyncServer extends Synchronizer<Server> {
	
	private final Server server;
	public final Lang lang;
	
	public AsyncServer(Server server, Lang lang) {
		super(server);
		this.server = server;
		this.lang = lang;
	}
	
	public boolean isShuttingDown() {
		return this.server.isStopping();
	}
}
