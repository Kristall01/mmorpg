package hu.kristall.rpg.command.commands.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.world.Item;
import hu.kristall.rpg.world.Material;

public class CommandWorldSpawnitem extends SimpleCommand {
	
	public CommandWorldSpawnitem(CommandParent parent) {
		super(parent, "spawnitem", "<item> [name]", "spawns an item to your position");
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
				for (Material value : Material.values()) {
					names.append(", ");
					names.append(value.name());
				}
				entityHuman.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat(names.substring(2)));
				return;
			}
			Position pos = entityHuman.getPosition();
			String name = null;
			if(args.length > 1) {
				name = args[1];
			}
			Material m;
			try {
				m = Material.valueOf(args[0]);
			}
			catch (IllegalArgumentException ex) {
				entityHuman.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat("nincs ilyen item"));
				return;
			}
			entityHuman.getWorld().spawnItem(new Item(m, name), pos);
			entityHuman.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat("item spawned"));
		});
	}
}
