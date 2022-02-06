package hu.kristall.rpg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SynchronizedObject<T extends SynchronizedObject<T>> implements ISynchronized<T> {
	
	private final ExecutorService executor;
	private final Synchronizer<T> synchronizer;
	
	public SynchronizedObject() {
		executor = Executors.newSingleThreadExecutor();
		executor.submit(Utils.emptyRunnable);
		synchronizer = new Synchronizer<T>((T) this);
	}
	
	public Future<?> runTask(Runnable r) {
		return executor.submit(r);
	}
	
	protected void shutdown() {
		executor.shutdown();
	}
	
	public Synchronizer<T> getSynchronizer() {
		return synchronizer;
	}
	
}
