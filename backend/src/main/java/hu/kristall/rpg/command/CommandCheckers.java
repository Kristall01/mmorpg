package hu.kristall.rpg.command;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.WorldPlayer;
import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.function.Consumer;

public class CommandCheckers {
	
	public static void checkPlayerSender(CommandSender sender, Consumer<Player> playerConsumer) {
		if(!(sender instanceof PlayerSender)) {
			sender.sendTranslatedMessage("cli.players-only");
			return;
		}
		playerConsumer.accept((Player) sender);
	}
	
	public static void checkWorldPlayer(CommandSender sender, Consumer<WorldPlayer> wpTask) {
		checkPlayerSender(sender, player -> {
			try {
				((Player)sender).getAsyncEntity().sync(wp -> {
					if(wp == null) {
						sender.sendTranslatedMessage("cli.only-in-worlds");
						return;
					}
					wpTask.accept(wp);
				});
			}
			catch (Synchronizer.TaskRejectedException e) {
				//world cannot be shut down while players are online
				e.printStackTrace();
			}
		});
	}
	
	public static void checkWorldPlayerEntity(CommandSender sender, Consumer<EntityHuman> human) {
		checkWorldPlayer(sender, (wp) -> {
			if(!wp.hasEntity()) {
				sender.sendTranslatedMessage("cli.only-with-entity");
				return;
			}
			human.accept(wp.getEntity());
		});
	}
	
	public static boolean checkArgCount(CommandSender sender, String[] arguments, int minArgs) {
		if(arguments.length < minArgs) {
			sender.sendTranslatedMessage("cli.few-params");
			return false;
		}
		return true;
	}
	
}
