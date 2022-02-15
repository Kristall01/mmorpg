package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;

import java.util.*;

public class CommandHandler implements ICommand, CommandParent {
	
	private final String name;
	private Map<String, ICommand> commandMap = new TreeMap<>();
	private String description, helpEntry, totalPath;
	private CommandParent parent;
	private String requiredPermission;
	private Server server;
	
	public CommandHandler(CommandMap map, String name, String description) {
		this.parent = map;
		this.name = name;
		this.description = description;
		this.server = map.getServer();
		
		
		this.totalPath = '/'+name+' ';
		
		this.helpEntry = CommandUtils.buildSimpleHelpEntry(this);
	}
	
	private CommandHandler(CommandParent parent, String name, String description) {
		this.parent = parent;
		
		this.name = name;
		this.description = description;
		this.totalPath = parent.getTotalPath()+name+' ';
		
		this.helpEntry = CommandUtils.buildSimpleHelpEntry(this);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public String getRequiredPermission() {
		return requiredPermission;
	}
	
	public void setRequiredPermission(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}
	
	public void registerCommand(ICommand command) {
		commandMap.put(command.getName().toLowerCase(), command);
	}
	
	public CommandHandler registerSubHandler(String name, String description) {
		CommandHandler h = new CommandHandler(this, name, description);
		registerCommand(h);
		return h;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getArgs() {
		return null;
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		if(!executionAllowed(sender)) {
			sender.sendRawMessage(getServer().getLang(), "cil.error.no-perm");
			return;
		}
		if(args.length == 0) {
			showCommands(sender, label);
			return;
		}
		ICommand cmd = commandMap.get(args[0]);
		if(cmd == null) {
			sender.sendRawMessage(getServer().getLang(), "cil.error.unknown-command");
			return;
		}
		cmd.execute(sender, label, Arrays.copyOfRange(args, 1, args.length));
	}
	
	public void showCommands(CommandSender sender, String label) {
		sender.sendRawMessage(getServer().getLang(), "cil.handler.list", totalPath);
		for (ICommand e : commandMap.values()) {
			sender.sendMessage(e.toHelpEntry());
		}
	}
	
	public String getTotalPath() {
		return totalPath;
	}
	
	@Override
	public CommandParent getParent() {
		return parent;
	}
	
	@Override
	public Collection<ICommand> getRegisteredCommands() {
		return Collections.unmodifiableCollection(this.commandMap.values());
	}
	
	@Override
	public Collection<String> getRegisteredCommandNames() {
		return Collections.unmodifiableCollection(commandMap.keySet());
	}
	
	@Override
	public boolean executionAllowed(CommandSender sender) {
		return requiredPermission == null || sender.hasPermission(this.requiredPermission);
	}
	
	@Override
	public String toHelpEntry() {
		return helpEntry;
	}
	
	@Override
	public ICommand getCommand(String name) {
		return commandMap.get(name);
	}
	
	@Override
	public Server getServer() {
		return server;
	}
}
