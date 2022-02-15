package hu.kristall.rpg.sync;

public interface ISynchronized<T extends ISynchronized<T>> extends TaskRunner {
	
	Synchronizer<T> getSynchronizer();
	
}
