package hu.kristall.rpg.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrimitiveReader implements CommandSupplier {
	
	private BufferedReader reader;
	
	public PrimitiveReader() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	@Override
	public void close() throws IOException {
		reader.close();
	}
	
	@Override
	public String get() throws IOException {
		return reader.readLine();
	}
	
}
