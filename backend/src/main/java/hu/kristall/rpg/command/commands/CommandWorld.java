package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.WorldPosition;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;

public class CommandWorld extends SimpleCommand {
	public CommandWorld(CommandParent parent) {
		super(parent, "changeworld", "<world name>", "világ váltása");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			sender.sendTranslatedMessage("command.changeworld.worlds");
			sender.sendMessage(String.join(", ",getServer().getWorldsManager().getWorldNames()));
			return;
		}
		CommandCheckers.checkPlayerSender(sender, player -> {
			WorldPosition wp = null;
			if(!args[0].contentEquals("null")) {
				Synchronizer<World> asyncWorld = getServer().getWorldsManager().getWorld(args[0]);
				if(asyncWorld == null) {
					sender.sendTranslatedMessage("command.changeworld.no-such-world");
					return;
				}
				wp = new WorldPosition(asyncWorld, null);
			}
			sender.sendTranslatedMessage("command.changeworld.prepare");
			player.scheduleWorldChange(wp);
		});
		/*
		Player p;
		if(!(sender instanceof Player)) {
			if(!CommandCheckers.checkArgCount(sender, args, 2)) {
				return;
			}
			p = getServer().getPlayer(args[1]);
			if(p == null) {
				sender.sendMessage("§cHiba: §4Nincs ilyen player.");
				return;
			}
		}
		else {
			p = (Player) sender;
		}
		*/
	}
}
