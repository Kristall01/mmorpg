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
		System.out.println(translateColorCodes(message));
		System.out.print(ChatColor.RESET.ansiCode);
	}
	
	@Override
	public Server getServer() {
		return server;
	}
	
	private String translateColorCodes(String message) {
		int index = message.indexOf('§');
		if(index == -1) {
			return message;
		}
		
		char[] chars = message.toCharArray();
		boolean flag = false;
		
		StringBuilder output = new StringBuilder(message.length()*2);
		
		for(int i = 0; i < chars.length; ++i) {
			if(chars[i] == '§') {
				flag = !flag;
				continue;
			}
			if(!flag) {
				output.append(chars[i]);
				continue;
			}
			flag = false;
			ChatColor color = ChatColor.findByChar(chars[i]);
			if(color != null) {
				output.append(color.ansiCode);
			}
		}
		return output.toString();
	}
	
}
