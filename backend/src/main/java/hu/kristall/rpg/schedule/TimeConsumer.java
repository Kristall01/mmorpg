package hu.kristall.rpg.schedule;

public interface TimeConsumer {
	
	public abstract Timer getTimer();
	public abstract void cancel();
	public abstract boolean readyToRemove();
	
	public abstract void tick(long timeProgress);
	
}
