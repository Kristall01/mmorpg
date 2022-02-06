package hu.kristall.rpg.command;

import hu.kristall.rpg.command.senders.CommandSender;

public interface ICommand extends Executeable {
	
	String getName();
	String getDescription();
	String getArgs();
	void execute(CommandSender sender, String label, String[] args);
	String toHelpEntry();
	CommandParent getParent();
	
}
