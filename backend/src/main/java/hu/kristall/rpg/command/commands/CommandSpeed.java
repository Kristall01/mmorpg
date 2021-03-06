package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.Entity;

public class CommandSpeed extends SimpleCommand {
	public CommandSpeed(CommandParent parent) {
		super(parent, "speed", "<speed>", "sebesség átállítás");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof PlayerSender)) {
			sender.sendMessage("§cHiba: §4Ezt a parancsot csak játékosok használhatják.");
			return;
		}
		if(args.length == 0) {
			sender.sendMessage("§cHiba: §4Hibás parancs használat.");
			return;
		}
		double newSpeed;
		try {
			newSpeed = Double.parseDouble(args[0]);
		}
		catch (NumberFormatException ex) {
			sender.sendMessage("§cHiba: §c'"+args[0]+"'§4 nem értelmezhető számként.");
			return;
		}
		try {
			((Player)sender).getAsyncEntity().sync(wp -> {
				if(wp == null) {
					sender.sendMessage("§cHiba: §4Ezt a parancsot csak akkor lehet használni, ha egy világ része vagy.");
					return;
				}
				Entity entity = wp.getEntity();
				if(entity == null) {
					sender.sendMessage("§cHiba: §4Nincs entitásod");
					return;
				}
				try {
					entity.setSpeed(newSpeed);
				}
				catch (IllegalArgumentException ex) {
					sender.sendTranslatedMessage("command.speed.invalid-argument");
				}
				sender.sendMessage("§abeállítva");
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//world cannot be shut down while command is being processed
			e.printStackTrace();
		}
	}
	
}
