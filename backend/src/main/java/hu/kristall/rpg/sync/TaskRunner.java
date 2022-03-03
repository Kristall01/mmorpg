package hu.kristall.rpg.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface TaskRunner {
	
	Future<?> runTask(Runnable task);
	<T> Future<T> computeTask(Callable<T> c);
	boolean isShutdown();
	
}
