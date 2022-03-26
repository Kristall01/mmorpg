package hu.kristall.rpg.command.commands.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.world.Item;

public class CommandWorldSpawnitem extends SimpleCommand {
	
	public CommandWorldSpawnitem(CommandParent parent) {
		super(parent, "spawnitem", "<item>", "spawns an item to your position");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!CommandCheckers.checkArgCount(sender, args, 1)) {
			return;
		}
		CommandCheckers.checkWorldPlayerEntity(sender, entityHuman -> {
			Position pos = entityHuman.getPosition();
			entityHuman.getWorld().spawnItem(new Item(args[0]), pos);
			entityHuman.getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutChat("item spawned"));
		});
	}
}
