package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.world.entity.EntityHuman;

public class CommandDmg extends SimpleCommand {
	public CommandDmg(CommandParent parent) {
		super(parent, "dmg", "<amount>", null);
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof PlayerSender)) {
			sender.sendMessage("§cHiba: §4Ezt a parancsot csak játékosok használhatják.");
			return;
		}
		if(args.length < 1) {
			sender.sendMessage("§cHiba: §4Kevés paraméter.");
			return;
		}
		double amount;
		try {
			amount = Double.parseDouble(args[0]);
		}
		catch (NumberFormatException ex) {
			sender.sendMessage("§cHiba: §4Hibás számformátum");
			return;
		}
		((Player)sender).getAsyncEntity().sync(wp -> {
			if(wp == null) {
				sender.sendMessage("§cHiba: §4Ezt a parancsot csak akkor lehet használni, ha egy világ része vagy.");
				return;
			}
			EntityHuman entity = wp.getEntity();
			if(entity == null) {
				sender.sendMessage("§cHiba: §4Nincs entitásod");
				return;
			}
			if(amount < 0) {
				entity.damage(-amount);
			}
			else {
				entity.heal(amount);
			}
		});
	}
}
