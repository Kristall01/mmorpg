package hu.kristall.rpg.console;

import hu.kristall.rpg.Server;
import hu.kristall.rpg.command.senders.CommandSender;
import hu.kristall.rpg.sync.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
		Logger logger = LoggerFactory.getLogger("InputReader");
		try {
			if(System.console() == null) {
				throw new IOException("system console was not found");
			}
			this.supplier = new TerminalReader(this, asyncServer);
			logger.info("initializing input reader from tty terminal");
		}
		catch (IOException ex) {
			this.supplier = new PrimitiveReader();
			logger.info("initializing input reader from stdin");
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
			catch (Synchronizer.TaskRejectedException e) {
				//server is already shut down. let's stop this
				stop();
			}
		};
		
		try {
			asyncServer.sync(srv -> srv.addShutdownListener(srvInstance -> this.stop()));
		}
		catch (Synchronizer.TaskRejectedException e) {
			//server is already shut down, no need to add listener
			e.printStackTrace();
			stop();
			return;
		}
		
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
		if(stopping) {
			return;
		}
		stopping = true;
		supplier.close();
		if(t != null) {
			t.interrupt();
		}
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
