package hu.kristall.rpg.schedule;

public abstract class RemoveFlagConsumer implements TimeConsumer {

	private final Timer timer;
	private boolean remove = false;
	
	public RemoveFlagConsumer(Timer timer) {
		this.timer = timer;
	}
	
	@Override
	public Timer getTimer() {
		return this.timer;
	}
	
	@Override
	public void cancel() {
		this.remove = true;
	}
	
	@Override
	public boolean readyToRemove() {
		return remove;
	}
	
}
