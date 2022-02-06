package hu.kristall.rpg.console;

import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class TerminalReader implements CommandSupplier {
	
	private LineReader reader;
	private Terminal t;
	
	public TerminalReader(Completer c) throws IOException {
		t = TerminalBuilder.terminal();
		reader = LineReaderBuilder.builder()
			.terminal(t)
			.completer(c)
			.build();
	}
	
	@Override
	public String get() throws IOException {
		try {
			return reader.readLine("> ");
		}
		catch (EndOfFileException ex) {
			System.out.print("exit");
			return "exit";
		}
		catch (Throwable ex) {
			throw new IOException(ex);
		}
	}
	
	@Override
	public void close() throws IOException {
		t.close();
	}
}
