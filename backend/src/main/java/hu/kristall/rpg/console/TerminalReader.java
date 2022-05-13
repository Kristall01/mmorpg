package hu.kristall.rpg.console;

import hu.kristall.rpg.ChatColor;
import hu.kristall.rpg.Server;
import hu.kristall.rpg.sync.Synchronizer;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class TerminalReader implements CommandSupplier {
	
	private LineReader reader;
	private Terminal t;
	private Synchronizer<Server> asyncServer;
	
	public TerminalReader(final InputReader inputReader, Synchronizer<Server> asyncServer) throws IOException {
		t = TerminalBuilder.terminal();
		Completer c = (reader, line, candidates) -> {
			try {
				String text = line.line();
				Collection<String> l = null;
				try {
					l = asyncServer.syncCompute(srv -> srv.getCommandMap().complete(srv.getCommandMap().getConsoleCommandSender(), text)).get();
					for (String s : l) {
						candidates.add(new Candidate(s));
					}
				}
				catch (Synchronizer.TaskRejectedException e) {
					//server was shut down meanwhile waiting
				}
			}
			catch (InterruptedException | ExecutionException ignored) {}
		};
		
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
			return "stop";
		}
		catch (Throwable ex) {
			throw new IOException(ex);
		}
	}
	
	@Override
	public void close() {
		try {
			t.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(String message) {
		this.t.writer().println(ChatColor.translateColorCodes(message));
	}
	
}
