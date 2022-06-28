package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.SimpleCommand;
import hu.kristall.rpg.command.senders.CommandSender;

public class CommandFreeze extends SimpleCommand {
	
	public CommandFreeze(CommandParent parent) {
		super(parent, "freeze", null, "szál leállítása");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		if(!CommandCheckers.checkArgCount(sender, args, 1)) {
			return;
		}
		CommandCheckers.checkWorldPlayer(sender, worldPlayer -> {
			try {
				Thread.sleep(30000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
}
