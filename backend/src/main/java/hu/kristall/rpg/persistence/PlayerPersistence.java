package hu.kristall.rpg.persistence;

import hu.kristall.rpg.Utils;
import hu.kristall.rpg.sync.AsyncExecutor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class PlayerPersistence {
	
	private File playersDir;
	private final Logger logger;
	
	public PlayerPersistence(File base) throws IOException {
		this.playersDir = base;
		logger = LoggerFactory.getLogger("PlayerPersistence");
		if(!this.playersDir.mkdirs() && base.isDirectory()) {
			throw new IOException("failed to create player data directory");
		}
	}
	
	public synchronized SavedPlayer loadPlayer(String name) throws IOException {
		File f = new File(playersDir, name);
		if(!f.isFile()) {
			return null;
		}
		FileReader reader = new FileReader(f);
		SavedPlayer p = Utils.gson().fromJson(reader, SavedPlayer.class);
		reader.close();
		return p;
	}
	
	private synchronized void logError(Exception ex, String json, File target) {
		logger.error("failed to save to  '"+target.getName()+"' file the following SavedPlayer json: "+json,ex);
	}
	
	public void savePlayer(SavedPlayer sp) {
		AsyncExecutor.instance().runTask(() -> {
			String serialized = Utils.gson().toJson(sp);
			File target = new File(playersDir, sp.name);
			try {
				Files.writeString(target.toPath(), serialized);
			}
			catch (IOException e) {
				logError(e, serialized, target);
			}
		});
	}
	
}
