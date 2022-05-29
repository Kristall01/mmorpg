package hu.kristall.rpg.network;

import hu.kristall.rpg.sync.Cancelable;
import hu.kristall.rpg.sync.ISyncTimer;

public abstract class AutoRemove {
	
	private boolean removed = false;
	private Cancelable removeTask;
	
	public AutoRemove(ISyncTimer timer, long removeDelay) {
		this.removeTask = timer.schedule(c -> this.remove(), removeDelay);
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public final void remove() {
		removeTask.cancel();
		this.removed = true;
		this.remove0();
	}
	
	protected abstract void remove0();
	
}
