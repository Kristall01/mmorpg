package hu.kristall.rpg.command.commands;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.command.CommandCheckers;
import hu.kristall.rpg.command.CommandParent;
import hu.kristall.rpg.command.impl.AuthorizedCommand;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.network.packet.out.PacketOutSudo;

import java.util.Arrays;

public class CommandSudo extends AuthorizedCommand {
	
	public CommandSudo(CommandParent parent) {
		super("sudo", parent, "sudo", "<játékosnév VAGY *> <parancs>", "parans futtatása más játékos nevében");
	}
	
	@Override
	protected void checkedExecute(CommandSender sender, String label, String[] args) {
		CommandCheckers.checkArgCount(sender, args, 2);
		if(args[0].equals("*")) {
			for (Player player : sender.getServer().getPlayers()) {
				player.getConnection().sendPacket(new PacketOutSudo(String.join(" ",Arrays.copyOfRange(args, 1, args.length))));
			}
			return;
		}
		Player player = sender.getServer().getPlayer(args[0]);
		if(player == null) {
			sender.sendTranslatedMessage("command.sudo.player-not-found");
			return;
		}
		player.getConnection().sendPacket(new PacketOutSudo(String.join(" ",Arrays.copyOfRange(args, 1, args.length))));
	}
}
