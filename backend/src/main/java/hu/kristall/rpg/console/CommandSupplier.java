package hu.kristall.rpg.console;

import java.io.IOException;

public interface CommandSupplier {
	
	String get() throws IOException;
	void close();
	void sendMessage(String message);
	
}
