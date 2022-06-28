package hu.kristall.rpg.command.commands.inventory;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;

public class CommandInventoryOpen extends SimpleCommand
{
	public CommandInventoryOpen(CommandParent parent) {
		super(parent, "open", null, null);
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, entityHuman -> {
			if(CommandCheckers.checkArgCount(sender, args, 1)) {
				entityHuman.openInventory(args[0]);
			}
		});
	}
	
}
