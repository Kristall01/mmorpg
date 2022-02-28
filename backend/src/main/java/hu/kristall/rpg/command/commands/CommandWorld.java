package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.World;

public class CommandWorld extends SimpleCommand {
	public CommandWorld(CommandParent parent) {
		super(parent, "world", "<world name>", "change world");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cHiba: §4Ezt a parancsot csak játékosok használhatják.");
			return;
		}
		if(args.length == 0) {
			sender.sendMessage("§cHiba: §4Hibás parancs használat.");
			return;
		}
		Player p = (Player) sender;
		Synchronizer<World> w = getServer().getWorldsManager().getWorld(args[0]);
		if(w == null) {
			sender.sendMessage("§cHiba: §4Nincs ilyen világ");
			return;
		}
		p.scheduleWorldChange(w);
		p.sendMessage("Felkészülés a világ váltásra...");
	}
}