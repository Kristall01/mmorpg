package hu.kristall.rpg.command.senders;

import hu.kristall.rpg.ChatColor;
import hu.kristall.rpg.Server;

public class ConsoleCommandSender implements CommandSender {
	
	private Server server;
	
	public ConsoleCommandSender(Server server) {
		this.server = server;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return true;
	}
	
	@Override
	public void sendMessage(String message) {
		System.out.println(ChatColor.translateColorCodes(message));
		System.out.print(ChatColor.RESET.ansiCode);
	}
	
	@Override
	public Server getServer() {
		return server;
	}
	
}
