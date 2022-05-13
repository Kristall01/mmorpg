package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.ConsoleCommandSender;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandMap implements CommandParent {

	protected Map<String, ICommand> commandMap = new HashMap<>();
	private final String[] emptyArgs = new String[0];
	private ConsoleCommandSender consoleCommandSender;
	private Server server;
	
	public CommandMap(Server server) {
		this.server = server;
		consoleCommandSender = new ConsoleCommandSender(server);
	}
	
	public Server getServer() {
		return server;
	}
	
	public void registerCommand(ICommand command) {
		commandMap.put(command.getName(), command);
	}
	
	@Override
	public CommandHandler registerSubHandler(String name, String description) {
		CommandHandler h = new CommandHandler(this, name, description);
		registerCommand(h);
		return h;
	}
	
	public ICommand getCommand(String prefix) {
		return commandMap.get(prefix.toLowerCase());
	}
	
	public void executeCommand(CommandSender sender, String command) {
		if(command.isEmpty()) {
			return;
		}
		command = command.trim();
		int firstSpace = command.indexOf(' ');
		ICommand cmd;
		String prefix;
		String[] args;
		if(firstSpace == -1) {
			prefix = command;
			args = emptyArgs;
		}
		else {
			prefix = command.substring(0, firstSpace);
			args = Utils.fsplit(command.substring(firstSpace+1), ' ');
		}
		cmd = getCommand(prefix);
		if(cmd == null) {
			sender.sendTranslatedMessage("cil.error.unknown-command");
			return;
		}
		try {
			cmd.execute(sender, prefix, args);
		}
		catch (Throwable t) {
			sender.sendTranslatedMessage("cil.error.exception");
		}
	}
	
	public void executeConsoleCommand(String command) {
		executeCommand(consoleCommandSender, command);
	}
	
	@Override
	public String getTotalPath() {
		return "";
	}
	
	@Override
	public Collection<ICommand> getRegisteredCommands() {
		return Collections.unmodifiableCollection(this.commandMap.values());
	}
	
	@Override
	public boolean executionAllowed(CommandSender sender) {
		return true;
	}
	
	@Override
	public Collection<String> getRegisteredCommandNames() {
		return Collections.unmodifiableCollection(this.commandMap.keySet());
	}
	
	public Collection<String> complete(CommandSender sender, String line) {
		if(line.length() == 0) {
			return tabComplete(sender, new String[0], 0);
		}
		else {
			return tabComplete(sender, Utils.fsplit(line, ' '), 0);
		}
	}
	
	public ConsoleCommandSender getConsoleCommandSender() {
		return consoleCommandSender;
	}
}
