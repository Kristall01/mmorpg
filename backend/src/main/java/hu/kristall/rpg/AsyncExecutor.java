package hu.kristall.rpg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncExecutor {
	
	private static final ExecutorService executor;
	
	static {
		executor = Executors.newCachedThreadPool((r) -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("shutting down AsyncExecutor");
			executor.shutdown();
			try {
				//ultimatum to shut down
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				System.out.println("AsyncExecutor ultimatum was interrupted");
				e.printStackTrace();
			}
			executor.shutdownNow();
		}));
	}
	
	public static Future<?> submit(Runnable r) {
		return executor.submit(r);
	}

}
