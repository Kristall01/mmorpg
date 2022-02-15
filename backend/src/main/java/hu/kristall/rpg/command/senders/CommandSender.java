package hu.kristall.rpg.command.senders;

import hu.kristall.rpg.lang.Lang;

public interface CommandSender {
	
	boolean hasPermission(String permission);
	void sendMessage(String message);
	default void sendRawMessage(Lang lang, String message, String... args) {
		sendMessage(lang.getMessage(message, args));
	}
	
}
