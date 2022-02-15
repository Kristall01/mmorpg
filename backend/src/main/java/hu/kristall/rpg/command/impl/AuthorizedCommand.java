package hu.kristall.rpg.command.impl;

import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.senders.CommandSender;

public abstract class AuthorizedCommand extends SimpleCommand {

	private String permission;
	
	public AuthorizedCommand(String permission, CommandParent parent, String name, String args, String description) {
		super(parent, name, args, description);
		this.permission = permission;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	@Override
	public boolean executionAllowed(CommandSender sender) {
		return permission == null || sender.hasPermission(this.permission);
	}
}
