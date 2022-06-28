package hu.kristall.rpg.command.commands.world;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.entity.Entity;
import hu.kristall.rpg.world.entity.EntityType;

import java.util.List;

public class CommandKillall extends SimpleCommand {
	public CommandKillall(CommandParent parent) {
		super(parent, "killall", null, "entitások törlése a pályáról");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, entity -> {
			for (Entity entity1 : List.copyOf(entity.getWorld().getEntities())) {
				if(entity1.type() != EntityType.HUMAN) {
					entity1.kill();
				}
			}
		});
	}
	
}
