package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;

public class CommandWorld extends SimpleCommand {
	public CommandWorld(CommandParent parent) {
		super(parent, "changeworld", "<world name> [player]", "change world");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("Ezekbe a világokba lehet csatlakozni:");
			sender.sendMessage(String.join(", ",getServer().getWorldsManager().getWorldNames()));
			return;
		}
		Synchronizer<World> w = getServer().getWorldsManager().getWorld(args[0]);
		if(w == null && !args[0].contentEquals("null")) {
			sender.sendMessage("§cHiba: §4Nincs ilyen világ");
			return;
		}
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
		p.sendMessage("Felkészülés a világ váltásra...");
		p.scheduleWorldChange(w);
	}
}
