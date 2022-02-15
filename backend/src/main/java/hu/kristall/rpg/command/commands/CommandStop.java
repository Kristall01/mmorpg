package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.AuthorizedCommand;
import hu.kristall.rpg.command.senders.CommandSender;

public class CommandStop extends AuthorizedCommand {
	
	public CommandStop(CommandParent parent) {
		super("*", parent, "stop", null, "stop server");
	}
	
	@Override
	public void checkedExecute(CommandSender sender, String label, String[] args) {
		getServer().shutdown();
	}
	
}
