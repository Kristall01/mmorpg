package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.command.senders.ConsoleCommandSender;
import hu.kristall.rpg.lang.Lang;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.*;

public class CommandMap implements CommandParent, Completer {

	protected Map<String, ICommand> commandMap = new HashMap<>();
	private final String[] emptyArgs = new String[0];
	private ConsoleCommandSender consoleCommandSender;
	private Server server;
	
	public CommandMap(Server server) {
		this.server = server;
		consoleCommandSender = new ConsoleCommandSender(server);
	}
	
	public Server getServer() {
		return server;
	}
	
	public void registerCommand(ICommand command) {
		commandMap.put(command.getName(), command);
	}
	
	public ICommand getCommand(String prefix) {
		return commandMap.get(prefix.toLowerCase());
	}
	
	public void executeCommand(CommandSender sender, String command) {
		if(command.isEmpty()) {
			return;
		}
		int firstSpace = command.indexOf(' ');
		ICommand cmd;
		String prefix;
		String[] args;
		if(firstSpace == -1) {
			prefix = command;
			args = emptyArgs;
		}
		else {
			prefix = command.substring(0, firstSpace);
			args = command.substring(firstSpace+1).split(" ");
		}
		cmd = getCommand(prefix);
		if(cmd == null) {
			sender.sendRawMessage("cil.error.unknown-command");
			return;
		}
		try {
			cmd.execute(sender, prefix, args);
		}
		catch (Throwable t) {
			sender.sendRawMessage("cil.error.exception");
		}
	}
	
	public void executeConsoleCommand(String command) {
		executeCommand(consoleCommandSender, command);
	}
	
	@Override
	public String getTotalPath() {
		return "";
	}
	
	@Override
	public Collection<ICommand> getRegisteredCommands() {
		return Collections.unmodifiableCollection(this.commandMap.values());
	}
	
	@Override
	public boolean executionAllowed(CommandSender sender) {
		return true;
	}
	
	@Override
	public void complete(LineReader reader, ParsedLine pline, List<Candidate> candidates) {
		ArrayList<String> s = new ArrayList<>();
		String line = pline.line();
		if(line.length() == 0) {
			tabComplete(new String[0], 0, s);
		}
		else {
			tabComplete(Utils.fsplit(line, ' '), 0, s);
		}
		for (String s1 : s) {
			candidates.add(new Candidate(s1));
		}
	}
	
	public ConsoleCommandSender getConsoleCommandSender() {
		return consoleCommandSender;
	}
}
