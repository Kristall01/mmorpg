package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;

import java.util.Collection;
import java.util.Collections;

public interface CommandParent extends Executable {
	
	String getTotalPath();
	Collection<ICommand> getRegisteredCommands();
	Collection<String> getRegisteredCommandNames();
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
					sender.sendRawMessage(getServer().getLang(), "cil.handler.list", getTotalPath());
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
	default Collection<String> tabComplete(CommandSender sender, String[] args, int argsIndex) {
		if((args.length - argsIndex) <= 1) {
			return getRegisteredCommandNames();
		}
		ICommand cmd = getCommand(args[argsIndex]);
		if(cmd == null) {
			return Collections.emptyList();
		}
		return cmd.tabComplete(sender, args, argsIndex+1);
	}
}
