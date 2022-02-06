package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.impl.SimpleCommand;

public class CommandHelp extends SimpleCommand {
	
	public CommandHelp(CommandParent parent) {
		super(parent, "help", null, "parancsok listázása");
	}
	
	@Override
	public void checkedExecute(CommandSender sender, String label, String[] args) {
		getParent().showHelp(sender);
	}
	
}
