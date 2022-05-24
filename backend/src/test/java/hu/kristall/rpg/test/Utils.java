package hu.kristall.rpg.test;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.persistence.Savefile;
import hu.kristall.rpg.sync.Synchronizer;

import java.io.IOException;

public class Utils {

	private static int nextPort = 8000;
	private static final Object portLock = new Object();
	
	public static Synchronizer<Server> createTestServer(Savefile savefile) throws IOException {
		synchronized(portLock) {
			return Server.createServer(savefile, ++nextPort, null);
		}
	}

}
