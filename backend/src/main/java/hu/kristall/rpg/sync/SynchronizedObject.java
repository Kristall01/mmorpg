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
	private Synchronizer<T> synchronizer;
	private Logger taskPoolLogger;
	private SyncTimer timer;

	protected SynchronizedObject(String threadName, Synchronizer<T> syncer) {
		this.executor = Executors.newSingleThreadExecutor(r -> new Thread(r, threadName));
		executor.submit(Utils.emptyRunnable);
		if(syncer == null) {
			syncer = new Synchronizer<T>((T) this);
		}
		synchronizer = syncer;
		this.taskPoolLogger = LoggerFactory.getLogger(threadName+"-task-pool");
		this.timer = new SyncTimer(this);
	}
	
	protected void changeSyncer(Synchronizer<T> syncer) {
		this.synchronizer = syncer;
	}
	
	protected SynchronizedObject(String threadName) {
		this(threadName, null);
	}
	
	/*	protected SynchronizedObject() {
		this(Executors.newSingleThreadExecutor());
		this.taskPoolLogger = LoggerFactory.getLogger("unnamed-task-pool");
	}*/
	
	public ISyncTimer getTimer() {
		return timer;
	}
	
	public Future<?> runTask(Runnable r) {
		return executor.submit(r);
	}
	
	public <U> Future<U> computeTask(Callable<U> c) {
		return executor.submit(c);
	}
	
	@Override
	public boolean isShutdown() {
		return executor.isShutdown();
	}
	
	protected void shutdown() {
		synchronizer.changeObject(null);
		taskPoolLogger.info("shutting down...");
		executor.shutdown();
		timer.cancel();
		taskPoolLogger.info("shutdown done");
	}
	
	public Synchronizer<T> getSynchronizer() {
		return synchronizer;
	}
	
}
