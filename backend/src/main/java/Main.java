import hu.kristall.rpg.ChatColor;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.console.FilteredConsolePrinter;
import hu.kristall.rpg.console.InputReader;
import hu.kristall.rpg.network.NetworkServer;
import hu.kristall.rpg.sync.Synchronizer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	static String jarName() {
		try {
			return new File(Main.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI()
				.getPath()).getName();
		}
		catch (URISyntaxException e) {
			return "server.jar";
		}
	}
	
	public static void main(String[] args)  {
		/*
		Options opts = new Options();
		opts.addOption(Option.builder()
				.longOpt("port")
				.required(false)
				.hasArg(true)
				.desc("tcp port to run server on. defaults to 8080")
				.optionalArg(true)
				.valueSeparator('=')
				.numberOfArgs(1)
				.argName("port")
				.build());
		
		opts.addOption("h", "help", false, "print this help");
		opts.addOption(Option.builder()
				.longOpt("serve")
				.required(false)
				.hasArg(true)
				.valueSeparator('=')
				.desc("directory to serve static files from. skipping this argument will disable static file serving altogether")
				.numberOfArgs(1)
				.argName("path")
				.build());
		
		if(args.length == 0) {
			String jarname = jarName();
			new HelpFormatter().printHelp("java -jar "+jarname+" [options]", opts);
		}
		*/
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", /*"yyyy-MM-dd */"HH:mm:ss");
		System.setProperty("org.slf4j.simpleLogger.showLogName", "true");
		
		if(System.console() == null) {
			System.setOut(new FilteredConsolePrinter(System.out, ChatColor::stripColorCodes));
			System.setErr(new FilteredConsolePrinter(System.err, ChatColor::stripColorCodes));
		}
		else {
			System.setOut(new FilteredConsolePrinter(System.out, ChatColor::translateColorCodes));
			System.setErr(new FilteredConsolePrinter(System.err, ChatColor::translateColorCodes));
		}
		
		NetworkServer networkServer = new NetworkServer();
		Synchronizer<Server> s = Server.createServer();
		Map<String, Server> servers = new HashMap<String, Server>();
		networkServer.addServer("s0", s);
		networkServer.addServer("s1", s);
		networkServer.addServer("s2", s);
		InputReader reader = new InputReader(s);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			
			networkServer.stop();
			s.sync(srv -> {
				if(srv != null) {
					srv.shutdown();
				}
			});
		}));
	}
	
}
