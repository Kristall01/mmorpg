package hu.kristall.rpg;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Synchronizer<T extends ISynchronized<T>> {

	private final T taskRunner;
	private T returnedObject;
	private final Object syncLock = new Object();
	
	public Synchronizer(T t) {
		this.taskRunner = t;
		this.returnedObject = t;
	}
	
	public Future<?> sync(Consumer<T> task) {
		return taskRunner.runTask(() -> {
			synchronized(syncLock) {
				task.accept(returnedObject);
			}
		});
	}
	
	public void changeObject(T newObject) {
		synchronized(syncLock) {
			this.returnedObject = newObject;
		}
	}

}
