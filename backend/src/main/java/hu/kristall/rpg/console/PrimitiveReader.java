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
	public void close() {
		try {
			reader.close();
		}
		catch (IOException ignored) {}
	}
	
	@Override
	public void sendMessage(String message) {
		System.out.println(message);
	}
	
	@Override
	public String get() throws IOException {
		return reader.readLine();
	}
	
}
