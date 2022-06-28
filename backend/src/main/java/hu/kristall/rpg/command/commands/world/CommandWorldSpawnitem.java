package hu.kristall.rpg.command.commands.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;
import hu.kristall.rpg.world.item.ItemGenerator;

public class CommandWorldSpawnitem extends SimpleCommand {
	
	public CommandWorldSpawnitem(CommandParent parent) {
		super(parent, "spawnitem", "<item>", "tárgy lerakása a helyedre");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, entityHuman -> {
			if(args.length == 0) {
				Material[] mats = Material.values();
				if(mats.length == 0) {
					entityHuman.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat("Nincs lerakható item."));
					return;
				}
				StringBuilder names = new StringBuilder();
				for (String type : entityHuman.getWorld().getItemMap().getItemTypes()) {
					names.append(", ");
					names.append(type);
				}
				entityHuman.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat(names.substring(2)));
				return;
			}
			Position pos = entityHuman.getPosition();
			ItemGenerator itemSupplier = sender.getServer().getItemMap().getItem(args[0]);
			if(itemSupplier == null) {
				sender.sendTranslatedMessage("error.item-not-found");
				return;
			}
			Item i = itemSupplier.generateItem();
			if(i == null) {
				sender.sendTranslatedMessage("cerror.item-not-available");
				return;
			}
			entityHuman.getWorld().spawnItem(i, pos);
			sender.sendTranslatedMessage("command.world.spawnitem.done");
		});
	}
}
