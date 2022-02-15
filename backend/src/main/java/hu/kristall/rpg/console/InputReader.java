package hu.kristall.rpg.console;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.sync.Synchronizer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class InputReader implements CommandSender {
	
	private Thread t;
	private Consumer<String> consumer;
	private CommandSupplier supplier;
	private Synchronizer<Server> asyncServer;
	private boolean stopping = false;
	
	public InputReader(Synchronizer<Server> asyncServer) {
		this.asyncServer = asyncServer;
		
		try {
			if(System.console() == null) {
				throw new IOException("system console was not found");
			}
			this.supplier = new TerminalReader(this, asyncServer);
			System.out.println("initializing input reader from Terminal");
		}
		catch (IOException ex) {
			this.supplier = new PrimitiveReader();
			System.out.println("initializing input reader from System.in");
		}
		
		this.consumer = (cmdline) -> {
			try {
				asyncServer.sync(srv -> {
					if(srv != null) {
						srv.getCommandMap().executeCommand(this, cmdline);
					}
				}).get();
			}
			catch (InterruptedException | ExecutionException e) {
				if(!stopping) {
					e.printStackTrace();
				}
			}
		};
		
		asyncServer.sync(srv -> srv.addShutdownListener(srvInstance -> this.stop()));
		
		t = new Thread(this::run, "InputReader thread");
		t.start();
	}
	
	private void run() {
		while (!stopping) {
			try {
				String line = supplier.get();
				if(line == null) {
					break;
				}
				consumer.accept(line);
			}
			catch (IOException e) {
				if(!stopping) {
					e.printStackTrace();
					System.out.println("failed to read input from terminal/stdin");
					consumer.accept("stop");
				}
				break;
			}
		}
	}
	
	public void stop() {
		stopping = true;
		supplier.close();
		t.interrupt();
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return true;
	}
	
	@Override
	public void sendMessage(String message) {
		supplier.sendMessage(message);
	}
	
	public Synchronizer<Server> getAsyncServer() {
		return asyncServer;
	}
	
}
