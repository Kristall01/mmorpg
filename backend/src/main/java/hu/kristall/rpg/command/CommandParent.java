package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;

import java.util.Collection;
import java.util.List;

public interface CommandParent extends Executeable {
	
	String getTotalPath();
	Collection<ICommand> getRegisteredCommands();
	ICommand getCommand(String name);
	
	Server getServer();
	
	default void showHelp(CommandSender sender) {
		Collection<ICommand> cmds = getRegisteredCommands();
		String emptyListMessage = getServer().getLang().getMessage("cil.error.no-commands");
		if(cmds.isEmpty()) {
			sender.sendMessage(emptyListMessage);
			return;
		}
		boolean helpSent = false;
		for (ICommand cmd : cmds) {
			if(cmd.executionAllowed(sender)) {
				if(!helpSent) {
					sender.sendRawMessage("cil.handler.list", getTotalPath());
					helpSent = true;
				}
				sender.sendMessage(cmd.toHelpEntry());
			}
		}
		if(!helpSent) {
			sender.sendMessage(emptyListMessage);
		}
	}
	
	@Override
	default void tabComplete(String[] args, int argsIndex, List<String> candidates) {
		if((args.length - argsIndex) <= 1) {
			for (ICommand registeredCommand : getRegisteredCommands()) {
				candidates.add(registeredCommand.getName());
			}
			return;
		}
		ICommand cmd = getCommand(args[argsIndex]);
		if(cmd == null) {
			return;
		}
		cmd.tabComplete(args, argsIndex+1, candidates);
	}
}
