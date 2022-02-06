package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.commands.CommandExit;
import hu.kristall.rpg.command.commands.CommandHelp;

public class CommandCollections {
	
	public static CommandMap base(Server server) {
		CommandMap map = new CommandMap(server);
		map.registerCommand(new CommandHelp(map));
		map.registerCommand(new CommandExit(map));
		return map;
	}
	
}
