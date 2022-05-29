import hu.kristall.rpg.ChatColor;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.console.FilteredConsolePrinter;
import hu.kristall.rpg.console.InputReader;
import hu.kristall.rpg.network.config.ClasspathConfigurator;
import hu.kristall.rpg.network.config.FilesystemHostConfigurator;
import hu.kristall.rpg.network.config.HostConfigurator;
import hu.kristall.rpg.persistence.Savefile;
import hu.kristall.rpg.sync.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Main {
	
	public static void main(String[] args) throws IOException {
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
		HostConfigurator hostConfigurator = null;
		Logger logger = LoggerFactory.getLogger("main");
		String servePath = System.getenv("serve");
		if(servePath != null) {
			hostConfigurator = new FilesystemHostConfigurator(servePath);
			logger.info("frontend hosztolás bekapcsolva a '"+servePath+"' mappából.");
		}
		else {
			try {
				InputStream frontendResource = Main.class.getResourceAsStream("frontend");
				if(frontendResource != null) {
					frontendResource.close();
					hostConfigurator = new ClasspathConfigurator("/frontend");
					logger.info("frontend hosztolás bekapcsolva a jar '/frontend' mappából.");
				}
			}
			catch (IOException ignored) {}
		}
		String sourcePath = System.getenv("savefile");
		Savefile savefile;
		InputStream in = sourcePath != null ? new FileInputStream(sourcePath) : Main.class.getResourceAsStream("/savefile.json");
		try (in) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			savefile = Utils.gson().fromJson(reader, Savefile.class);
		}
		Synchronizer<Server> s = Server.createServer(savefile, 8080, hostConfigurator);
		InputReader reader = new InputReader(s);
		
	}
	
}
