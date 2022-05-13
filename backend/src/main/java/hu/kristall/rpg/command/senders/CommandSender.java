package hu.kristall.rpg.command.senders;

import hu.kristall.rpg.Server;

public interface CommandSender {
	
	boolean hasPermission(String permission);
	void sendMessage(String message);
	default void sendTranslatedMessage(String message, String... args) {
		sendMessage(getServer().getLang().getMessage(message, args));
	}
	
	Server getServer();
	
}
