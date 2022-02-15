package hu.kristall.rpg.command.senders;

import hu.kristall.rpg.ChatColor;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.sync.Synchronizer;

public class ConsoleCommandSender implements CommandSender {
	
	private Synchronizer<Server> asyncServer;
	
	public ConsoleCommandSender(Synchronizer<Server> server) {
		this.asyncServer = server;
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
	
	public Synchronizer<Server> getAsyncServer() {
		return asyncServer;
	}
	
}
