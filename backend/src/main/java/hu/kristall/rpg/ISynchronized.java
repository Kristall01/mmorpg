package hu.kristall.rpg;

import java.util.concurrent.Future;

public interface ISynchronized<T extends ISynchronized<T>> {
	
	Future<?> runTask(Runnable task);
	Synchronizer<T> getSynchronizer();
	
}
