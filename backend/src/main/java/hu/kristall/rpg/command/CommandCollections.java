package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.commands.*;
import hu.kristall.rpg.command.commands.inventory.CommandInventoryAdd;
import hu.kristall.rpg.command.commands.inventory.CommandInventoryListitems;
import hu.kristall.rpg.command.commands.world.CommandWorldDummy;
import hu.kristall.rpg.command.commands.world.CommandWorldPet;
import hu.kristall.rpg.command.commands.world.CommandWorldSpawnitem;

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
		map.registerCommand(new CommandSudo(map));
		
		CommandHandler inventory = map.registerSubHandler( "inventory", "inventory commands");
			inventory.registerCommand(new CommandInventoryListitems(inventory));
			inventory.registerCommand(new CommandInventoryAdd(inventory));
			
		CommandHandler world = map.registerSubHandler("world", "world commands");
			world.registerCommand(new CommandWorldSpawnitem(world));
			world.registerCommand(new CommandWorldDummy(world));
			world.registerCommand(new CommandWorldPet(world));
		
		return map;
	}
	
}
