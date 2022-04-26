package hu.kristall.rpg.command.commands.world;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.world.entity.EntityType;

public class CommandWorldDummy extends SimpleCommand {
	
	public CommandWorldDummy(CommandParent parent) {
		super(parent, "dummy", "[circle <N>]", "spawns (N) dummy at your location");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, h -> {
			if(args.length >= 2 && args[0].contentEquals("circle")) {
				int N;
				try {
					N = Integer.parseInt(args[1]);
					if(N < 1) {
						throw new NumberFormatException();
					}
				}
				catch (NumberFormatException ex) {
					sender.sendMessage("§cHiba: '" + args[1] + "'§c nem értelmezhető pozitív egész számként.");
					return;
				}
				Position center = h.getPosition();
				for(int i = 0; i < N; ++i) {
					h.getWorld().spawnEntity(EntityType.DUMMY, h.getWorld().getRandomPositionNear(center, 2, 2));
				}
			}
			else {
				h.getWorld().spawnEntity(EntityType.DUMMY, h.getPosition());
			}
		});
	}
	
}
