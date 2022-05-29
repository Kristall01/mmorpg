package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.AuthorizedCommand;
import hu.kristall.rpg.command.senders.CommandSender;

public class CommandThreads extends AuthorizedCommand {
	
	public CommandThreads(CommandParent parent) {
		super("*", parent, "threads", null, "szálak listázása");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		Thread.getAllStackTraces().keySet().forEach(s -> sender.sendMessage(s.getName()));
	}
	
}
