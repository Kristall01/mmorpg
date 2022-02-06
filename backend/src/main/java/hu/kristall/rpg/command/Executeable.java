package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;

import java.util.List;

public interface Executeable {

	boolean executionAllowed(CommandSender sender);
	default void tabComplete(String[] args, int argsIndex, List<String> candidates) {}
	Server getServer();
	
}
