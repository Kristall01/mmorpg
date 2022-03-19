package hu.kristall.rpg.sync;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class Synchronizer<T extends ISynchronized<T>> {

	private final TaskRunner taskRunner;
	private T returnedObject;
	private final Object syncLock = new Object();
	
	public Synchronizer(T t) {
		this.taskRunner = t;
		this.returnedObject = t;
	}
	
	public Synchronizer(T returnedObject, TaskRunner runner) {
		this.taskRunner = runner;
		this.returnedObject = returnedObject;
	}
	
	public Future<?> sync(Consumer<T> task) throws TaskRejectedException {
		synchronized(syncLock) {
			if(taskRunner.isShutdown()) {
				throw new TaskRejectedException();
			}
		}
		return taskRunner.runTask(() -> {
			try {
				synchronized(syncLock) {
					task.accept(returnedObject);
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		});
	}
	
	public <U> Future<U> syncCompute(Function<T, U> task) throws TaskRejectedException {
		synchronized(syncLock) {
			if(taskRunner.isShutdown()) {
				throw new TaskRejectedException();
			}
		}
		return taskRunner.computeTask(() -> {
			synchronized(syncLock) {
				return task.apply(returnedObject);
			}
		});
	}
	
	public void changeObject(T newObject) {
		synchronized(syncLock) {
			this.returnedObject = newObject;
		}
	}
	
	public static class TaskRejectedException extends Exception {}

}
