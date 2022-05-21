import hu.kristall.rpg.ChatColor;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.console.FilteredConsolePrinter;
import hu.kristall.rpg.console.InputReader;
import hu.kristall.rpg.persistence.Savefile;
import hu.kristall.rpg.sync.Synchronizer;

import java.io.*;
import java.net.URISyntaxException;

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
	
	public static void main(String[] args) throws IOException {
		/*List<List<String>> layers = List.of(List.of("GRASS","GRASS","GRASS"));
		SavedLevel defaultLevel = new SavedLevel("defaultWorld", 10, 10, layers, Collections.emptyList(), Collections.emptyList());
		
		Map<String, SavedLevel> levels = new HashMap<>();
		levels.put("defaultWorld", defaultLevel);
		Map<String, SavedPlayer> players = new HashMap<>();
		players.put("asd", new SavedPlayer("asd", "w0", new Position(0,0), 5, Collections.emptyList(), Collections.emptyList()));
		Savefile savefile = new Savefile(levels, players, "defaultWorld");
		
		Path f = new File("/home/dominik/Asztal/savefile.json").toPath();
		String serialized =  Utils.gson().toJson(savefile);
		Files.write(f, serialized.getBytes(StandardCharsets.UTF_8));
		try {
			savefile = Utils.gson().fromJson(Files.readString(f), Savefile.class);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("parsed savefile");
		System.out.println(savefile);
		System.exit(0);*/
		
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
		String servePath = System.getenv("serve");
		String sourcePath = System.getenv("savefile");
		Savefile savefile;
		InputStream in = sourcePath != null ? new FileInputStream(sourcePath) : Main.class.getResourceAsStream("/savefile.json");
		try (in) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			savefile = Utils.gson().fromJson(reader, Savefile.class);
		}
		Synchronizer<Server> s = Server.createServer(servePath, savefile, 8006);
		InputReader reader = new InputReader(s);
		
	}
	
}
