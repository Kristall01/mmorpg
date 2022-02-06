package hu.kristall.rpg.schedule;

public class RepeatingTask extends RemoveFlagConsumer  {
	
	private final Runnable taskToRun;
	private final long tickInterval;
	private long accumulator;
	
	public RepeatingTask(Timer timer, long tickInterval, Runnable taskToRun) {
		super(timer);
		
		this.tickInterval = tickInterval;
		
		this.taskToRun = taskToRun;
	}
	
	@Override
	public void tick(long timeProgress) {
		accumulator += timeProgress;
		while(accumulator / tickInterval >= 1) {
			accumulator -= tickInterval;
			try {
				getTimer().runTaskAsync(taskToRun);
			}
			catch (Exception ex) {
			
			}
		}
		
	}
	
}
