package hu.kristall.rpg.console;

import java.io.IOException;
import java.util.function.Consumer;

public class InputReader {
	
	private Thread t;
	private Consumer<String> consumer;
	private CommandSupplier supplier;
	
	public InputReader(Consumer<String> consumer, CommandSupplier supp) {
		this.consumer = consumer;
		this.supplier = supp;
		
		t = new Thread(this::run);
		t.start();
	}
	
	private void run() {
		while (true) {
			try {
				consumer.accept(supplier.get());
			}
			catch (IOException e) {
				System.out.println("failed to read input from terminal/stdin");
				consumer.accept("exit");
			}
		}
	}
	
}
