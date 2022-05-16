package hu.kristall.rpg;

import hu.kristall.rpg.sync.Synchronizer;

public class AsyncServer extends Synchronizer<Server> {
	
	private final Server server;
	
	public AsyncServer(Server server) {
		super(server);
		this.server = server;
	}
	
}
