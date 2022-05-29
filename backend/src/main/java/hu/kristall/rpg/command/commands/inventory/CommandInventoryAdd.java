package hu.kristall.rpg.command.commands.inventory;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;

import java.util.function.Supplier;

public class CommandInventoryAdd extends SimpleCommand {
	
	public CommandInventoryAdd(CommandParent parent) {
		super(parent, "add", "<type>", "tárgy hozzáadása az inventoryhoz");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!CommandCheckers.checkArgCount(sender, args, 1)) {
			return;
		}
		CommandCheckers.checkWorldPlayerEntity(sender, e -> {
			Supplier<Item> itemSupplier = sender.getServer().getItemMap().getItem(args[0]);
			if(itemSupplier == null) {
				sender.sendTranslatedMessage("error.item-not-found");
				return;
			}
			Item i = itemSupplier.get();
			if(i == null) {
				sender.sendTranslatedMessage("error.item-not-available");
				return;
			}
			e.getInventory().addItem(i, 1);
		});
	}
}
