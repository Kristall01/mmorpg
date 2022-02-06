package hu.kristall.rpg.command;

import hu.kristall.rpg.Server;

public class CommandUtils {
	
	public static String buildSimpleHelpEntry(ICommand cmd) {
		String args = cmd.getArgs();
		if(args == null) {
			args = "";
		}
		else {
			args = ' '+args;
		}
		String description = cmd.getDescription();
		if(description == null) {
			description = "";
		}
		else {
			description = ": "+description;
		}
		return cmd.getServer().getLang().getMessage("cil.cmd.entry", cmd.getParent().getTotalPath(), cmd.getName(), args, description);
		
		/*StringBuilder b = new StringBuilder();
		b.append("§7 - §a");
		b.append(cmd.getParent().getTotalPath());
		b.append(cmd.getName());
		String args = cmd.getArgs();
		if(args != null) {
			b.append(" §2");
			b.append(args);
		}
		String description = cmd.getDescription();
		if(description != null) {
			b.append(": §7");
			b.append(description);
		}
		return b.toString();*/
	}
	
}
