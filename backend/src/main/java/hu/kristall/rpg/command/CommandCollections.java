package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.commands.*;

public class CommandCollections {
	
	public static CommandMap base(Server server) {
		CommandMap map = new CommandMap(server);
		map.registerCommand(new CommandHelp(map));
		map.registerCommand(new CommandStop(map));
		map.registerCommand(new CommandThreads(map));
		map.registerCommand(new CommandSpeed(map));
		map.registerCommand(new CommandWorld(map));
		map.registerCommand(new CommandClothes(map));
		map.registerCommand(new CommandDmg(map));
		return map;
	}
	
}
