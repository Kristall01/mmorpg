package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.PlayerSender;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.cozy.Cloth;
import hu.kristall.rpg.world.entity.cozy.ClothPack;

public class CommandClothes extends SimpleCommand {
	
	public CommandClothes(CommandParent parent) {
		super(parent, "clothes", "<ruha1> <ruha2> ... <ruhaN>", "ruha kombináció beállítása");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("§akomplett ruhák:");
			for (Cloth value : Cloth.values()) {
				if(value.isComplete()) {
					sender.sendMessage(" -§7 §r"+value.name());
				}
			}
			
			sender.sendMessage("§afelsők:");
			for (Cloth value : Cloth.values()) {
				if(value.bitmap == 4) {
					sender.sendMessage(" -§7 §r"+value.name());
				}
			}
			
			sender.sendMessage("§aalsók:");
			for (Cloth value : Cloth.values()) {
				if(value.bitmap == 2) {
					sender.sendMessage(" -§7 §r"+value.name());
				}
			}
			
			sender.sendMessage("§acipők:");
			for (Cloth value : Cloth.values()) {
				if(value.bitmap == 1) {
					sender.sendMessage(" -§7 §r"+value.name());
				}
			}
			
			return;
		}
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
				entity.setClothes(new ClothPack(clothes));
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//world cannot be shut down while command is being processed
			e.printStackTrace();
		}
	}
	
}
