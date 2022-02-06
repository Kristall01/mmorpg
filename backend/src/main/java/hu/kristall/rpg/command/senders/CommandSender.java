package hu.kristall.rpg.command.senders;

import hu.kristall.rpg.Server;

public interface CommandSender {
	
	boolean hasPermission(String permission);
	void sendMessage(String message);
	
	Server getServer();
	default void sendRawMessage(String message, String... args) {
		getServer().getLang().getMessage(message, args);
	}
	
}
