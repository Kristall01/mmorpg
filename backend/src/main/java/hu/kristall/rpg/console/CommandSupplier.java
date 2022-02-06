package hu.kristall.rpg.console;

import java.io.Closeable;
import java.io.IOException;

public interface CommandSupplier extends Closeable {
	
	String get() throws IOException;
	
}
