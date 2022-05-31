package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;

public class CommandDmg extends SimpleCommand {
	public CommandDmg(CommandParent parent) {
		super(parent, "dmg", "<amount>", "gyógítás/sebzés");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkWorldPlayerEntity(sender, entityHuman -> {
			if(!CommandCheckers.checkArgCount(sender, args, 1)) {
				return;
			}
			double amount;
			try {
				amount = Double.parseDouble(args[0]);
			}
			catch (NumberFormatException ex) {
				sender.sendTranslatedMessage("command.dmg.invalid-number-format");
				return;
			}
			if(amount < 0) {
				entityHuman.damage(-amount);
			}
			else {
				entityHuman.heal(amount);
			}
		});
	}
}
