package hu.kristall.rpg.command.commands.inventory;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;

public class CommandInventoryAdd extends SimpleCommand {
	
	public CommandInventoryAdd(CommandParent parent) {
		super(parent, "add", "<type>", "tárgy hozzáadása az inventoryhoz");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!CommandCheckers.checkArgCount(sender, args, 1)) {
			return;
		}
		Material m;
		try {
			m = Material.valueOf(args[0]);
		}
		catch (IllegalArgumentException ex) {
			sender.sendTranslatedMessage("command.inventory-add.no-such-item");
			return;
		}
		CommandCheckers.checkWorldPlayerEntity(sender, e -> {
			e.getInventory().addItem(new Item(m), 1);
		});
	}
}
