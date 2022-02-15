package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;

import java.util.Collection;
import java.util.Collections;

public interface Executable {

	boolean executionAllowed(CommandSender sender);
	
	default Collection<String> tabComplete(CommandSender tabber, String[] args, int argsIndex) {
		return Collections.emptyList();
	}
	
	Server getServer();
	
}
