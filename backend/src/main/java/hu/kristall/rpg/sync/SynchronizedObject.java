package hu.kristall.rpg.sync;

import hu.kristall.rpg.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SynchronizedObject<T extends SynchronizedObject<T>> implements ISynchronized<T> {
	
	private final ExecutorService executor;
	private final Synchronizer<T> synchronizer;
	private Logger taskPoolLogger;
	
	protected SynchronizedObject(String threadName) {
		this(Executors.newSingleThreadExecutor(r -> new Thread(r, threadName)));
		this.taskPoolLogger = LoggerFactory.getLogger(threadName+"-task-pool");
	}
	
	protected SynchronizedObject() {
		this(Executors.newSingleThreadExecutor());
		this.taskPoolLogger = LoggerFactory.getLogger("unnamed-task-pool");
	}
	
	private SynchronizedObject(ExecutorService executor) {
		this.executor = executor;
		executor.submit(Utils.emptyRunnable);
		synchronizer = new Synchronizer<T>((T) this);
	}
	
	public Future<?> runTask(Runnable r) {
		return executor.submit(r);
	}
	
	public <U> Future<U> computeTask(Callable<U> c) {
		return executor.submit(c);
	}
	
	protected void shutdown() {
		taskPoolLogger.info("shutting down...");
		executor.shutdown();
		taskPoolLogger.info("shutdown done");
	}
	
	public Synchronizer<T> getSynchronizer() {
		return synchronizer;
	}
	
}
