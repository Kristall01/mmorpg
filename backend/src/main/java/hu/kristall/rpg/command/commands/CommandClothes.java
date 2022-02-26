package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.cozy.Cloth;
import hu.kristall.rpg.world.entity.cozy.ClothPack;

public class CommandClothes extends SimpleCommand {
	
	public CommandClothes(CommandParent parent) {
		super(parent, "clothes", "<ruha1> <ruha2> ... <ruhaN>", "ruha kombináció beállítása");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!(sender instanceof PlayerSender)) {
			sender.sendMessage("§cHiba: §4Ezt a parancsot csak játékosok használhatják.");
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
			Cloth[] clothes = new Cloth[args.length];
			for (int i = 0; i < args.length; ++i) {
				try {
					clothes[i] = Cloth.valueOf(args[i]);
				}
				catch (IllegalArgumentException ex) {
					sender.sendMessage("§cHiba: §4Nem létezik §c'"+args[i]+"'§4 nevű ruha.");
					return;
				}
			}
			ClothPack pack;
			try {
				pack = new ClothPack(clothes);
			}
			catch (IllegalArgumentException ex) {
				sender.sendMessage("§cHiba: §4Érvénytelen ruha kombináció");
				return;
			}
			entity.setClothes(pack);
		});
	}
	
}
