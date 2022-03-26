package hu.kristall.rpg.command.commands.inventory;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.Item;

public class CommandInventoryAdd extends SimpleCommand {
	
	public CommandInventoryAdd(CommandParent parent) {
		super(parent, "add", "<type>", "add item to inventory");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!CommandCheckers.checkArgCount(sender, args, 1)) {
			return;
		}
		CommandCheckers.checkWorldPlayerEntity(sender, e -> {
			e.getInventory().addItem(new Item(args[0]), 1);
		});
	}
}
