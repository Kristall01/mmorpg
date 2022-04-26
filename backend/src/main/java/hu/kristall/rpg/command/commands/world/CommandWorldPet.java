package hu.kristall.rpg.command.commands.world;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.entity.EntityType;

public class CommandWorldPet extends SimpleCommand {
	
	public CommandWorldPet(CommandParent parent) {
		super(parent, "pet", null, "pet készítése");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, entityHuman -> {
			entityHuman.getWorld().spawnEntity(EntityType.SLIME, entityHuman.getPosition(), entityHuman);
		});
	}
}
