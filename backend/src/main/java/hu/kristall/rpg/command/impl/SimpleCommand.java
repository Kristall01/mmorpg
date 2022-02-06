package hu.kristall.rpg.command.impl;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.CommandUtils;
import hu.kristall.rpg.command.ICommand;
import hu.kristall.rpg.command.senders.CommandSender;

public abstract class SimpleCommand implements ICommand {
	
	private String name, helpEntry, description, args;
	
	private CommandParent parent;
	private Server server;
	
	public SimpleCommand(CommandParent parent, String name, String args, String description) {
		this.parent = parent;
		this.name = name;
		this.description = description;
		this.args = args;
		
		this.server = parent.getServer();
		
		helpEntry = CommandUtils.buildSimpleHelpEntry(this);
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getArgs() {
		return args;
	}
	
	@Override
	public String toHelpEntry() {
		return helpEntry;
	}
	
	@Override
	public CommandParent getParent() {
		return parent;
	}
	
	@Override
	public boolean executionAllowed(CommandSender sender) {
		return true;
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		if(!executionAllowed(sender)) {
			sender.sendMessage(parent.getServer().getLang().getMessage("cil.error.no-perm"));
			return;
		}
		checkedExecute(sender, label, args);
	}
	
	@Override
	public Server getServer() {
		return server;
	}
	
	protected abstract void checkedExecute(CommandSender sender, String label, String[] args);
	
}
