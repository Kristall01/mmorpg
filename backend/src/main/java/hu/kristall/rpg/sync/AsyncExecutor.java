package hu.kristall.rpg.sync;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class AsyncExecutor implements TaskRunner {
	
	private static final AsyncExecutor executor;
	private final ExecutorService executorService;
	private final Logger logger = LoggerFactory.getLogger("AsyncExecutor-master");
	
	static {
		executor = new AsyncExecutor();
	}
	
	private AsyncExecutor() {
		this.executorService = Executors.newCachedThreadPool(new AsyncThreadFactory());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			long maxPatienceMs = 10000;
			logger.info("shutting down...");
			executorService.shutdown();
			try {
				if(!executorService.awaitTermination(maxPatienceMs, TimeUnit.MILLISECONDS)) {
					logger.warn("shutdown patience time ("+maxPatienceMs+"ms) exceeded. forcing shutdown using shutdownNow()");
					executorService.shutdownNow();
				}
				logger.info("shutdown done");
			}
			catch (InterruptedException e) {
				logger.error("await termination was interrupted. forcing shutdown using ExecutorService::shutdownNow()");
				e.printStackTrace();
				executorService.shutdownNow();
			}
		}));
	}
	
	public static AsyncExecutor instance() {
		return executor;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public Future<?> runTask(Runnable r) {
		return executorService.submit(() -> {
			try {
				r.run();
			}
			catch (Throwable t) {
				logger.error("failed to execute task", t);
				throw t;
			}
		});
	}
	
	@Override
	public boolean isShutdown() {
		return executorService.isShutdown();
	}
	
	public <U> Future<U> computeTask(Callable<U> c) {
		return executorService.submit(() -> {
			try {
				return c.call();
			}
			catch (Throwable t) {
				logger.error("failed to execute task", t);
				throw t;
			}
		});
	}
	
	private static class AsyncThreadFactory implements ThreadFactory {
		
		int asyncID = 0;
		
		@Override
		public Thread newThread(@NotNull Runnable r) {
			Thread t = new Thread(r, "AsyncExecutor-worker-"+(asyncID++));
			t.setDaemon(true);
			return t;
		}
	}
	
}
