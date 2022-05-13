package hu.kristall.rpg.command.commands.inventory;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.Inventory;
import hu.kristall.rpg.world.Item;

import java.util.Map;

public class CommandInventoryListitems extends SimpleCommand {
	
	public CommandInventoryListitems(CommandParent parent) {
		super(parent, "items", null, "inventory tárgyak listázása");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, entityHuman -> {
			Inventory inv = entityHuman.getInventory();
			if(inv.isEmpty()) {
				sender.sendTranslatedMessage("command.inventory-items.empty");
				return;
			}
			for (Map.Entry<Item, Integer> item : inv.getItems()) {
				sender.sendMessage(" - " + item.getKey().getType()+":"+item.getValue());
			}
		});
	}
}
