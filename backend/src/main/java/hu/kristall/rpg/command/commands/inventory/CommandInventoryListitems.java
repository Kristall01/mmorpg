package hu.kristall.rpg.command.commands.inventory;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.world.Inventory;
import hu.kristall.rpg.world.Item;

import java.util.Map;

public class CommandInventoryListitems extends SimpleCommand {
	
	public CommandInventoryListitems(CommandParent parent) {
		super(parent, "items", null, "list items of your inventory");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof PlayerSender)) {
			sender.sendMessage("§cHiba: §4Ezt a parancsot csak játékosok használhatják.");
			return;
		}
		try {
			((Player)sender).getAsyncEntity().sync(wp -> {
				if(wp == null) {
					sender.sendMessage("§cHiba: §4Ezt a parancsot csak akkor lehet használni, ha egy világ része vagy.");
					return;
				}
				if(!wp.hasEntity()) {
					sender.sendMessage("§cHiba: §4Ezt a parancsot csak akkor lehet használni, ha van entitásod");
					return;
				}
				Inventory inv = wp.getEntity().getInventory();
				if(inv.isEmpty()) {
					sender.sendMessage("Üres az inventoryd.");
					return;
				}
				for (Map.Entry<Item, Integer> item : inv.getItems()) {
					sender.sendMessage(" - " + item.getKey().getType()+":"+item.getValue());
				}
			});
		}
		catch (Exception e) {
			//world cannot be shut down while command is being processed
			e.printStackTrace();
		}
	}
}
