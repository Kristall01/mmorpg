package hu.kristall.rpg.command;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.WorldPlayer;
import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.function.Consumer;

public class CommandCheckers {
	
	public static void checkWorldPlayer(CommandSender sender, Consumer<WorldPlayer> wpTask) {
		if(!(sender instanceof PlayerSender)) {
			sender.sendMessage("§cHiba: §4Ezt a parancsot csak játékosok használhatják.");
			return;
		}
		try {
			((Player)sender).getAsyncEntity().sync(wp -> {
				if(wp == null) {
					sender.sendMessage("§cHiba: §4Ezt a parancsot csak akkor lehet használni, ha egy világ része vagy.");
					return;
				}
				wpTask.accept(wp);
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//world cannot be shut down while players are online
			e.printStackTrace();
		}
	}
	
	public static void checkWorldPlayerEntity(CommandSender sender, Consumer<EntityHuman> human) {
		checkWorldPlayer(sender, (wp) -> {
			if(!wp.hasEntity()) {
				sender.sendMessage("§cHiba: §4Ezt a parancsot csak akkor lehet használni, ha van entitásod");
				return;
			}
			human.accept(wp.getEntity());
		});
	}
	
	public static boolean checkArgCount(CommandSender sender, String[] arguments, int minArgs) {
		if(arguments.length < minArgs) {
			sender.sendMessage("§cHiba: §4Kevés paraméter.");
			return false;
		}
		return true;
	}
	
}
