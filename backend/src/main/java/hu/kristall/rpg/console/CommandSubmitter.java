package hu.kristall.rpg.console;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.Synchronizer;

import java.util.function.Consumer;

public class CommandSubmitter implements Consumer<String> {
	
	private final Synchronizer<Server> serverSynchronizer;
	
	public CommandSubmitter(Synchronizer<Server> syncer) {
		this.serverSynchronizer = syncer;
	}
	
	@Override
	public void accept(String s) {
		serverSynchronizer.sync(server -> {
			server.getCommandMap().executeConsoleCommand(s);
		});
	}
	
}