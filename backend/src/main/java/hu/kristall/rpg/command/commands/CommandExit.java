package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;

public class CommandExit extends SimpleCommand {
	
	public CommandExit(CommandParent parent) {
		super(parent, "exit", null, "calls System.exit(0)");
	}
	
	@Override
	public void checkedExecute(CommandSender sender, String label, String[] args) {
		System.exit(0);
	}
	
}
