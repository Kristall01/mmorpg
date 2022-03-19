package hu.kristall.rpg.sync;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SyncTimer extends Timer implements ISyncTimer {
	
	private final TaskRunner runner;
	
	public SyncTimer(String threadName, TaskRunner runner) {
		super(threadName);
		this.runner = runner;
	}
	
	@Override
	public Cancelable schedule(Runnable task, long delay, long period) {
		SyncTask t = new SyncTask(runner, task);
		super.schedule(t, delay, period);
		return t;
	}
	
	@Override
	public Cancelable schedule(Runnable task, Date time) {
		SyncTask t = new SyncTask(runner, task);
		super.schedule(t, time);
		return t;
	}
	
	@Override
	public Cancelable schedule(Runnable task, Date firstTime, long period) {
		SyncTask t = new SyncTask(runner, task);
		super.schedule(t, firstTime, period);
		return t;
	}
	
	@Override
	public Cancelable scheduleAtFixedRate(Runnable task, long delay, long period) {
		SyncTask t = new SyncTask(runner, task);
		super.scheduleAtFixedRate(t, delay, period);
		return t;
	}
	
	@Override
	public Cancelable scheduleAtFixedRate(Runnable task, Date firstTime, long period) {
		SyncTask t = new SyncTask(runner, task);
		super.scheduleAtFixedRate(t, firstTime, period);
		return t;
	}
	
	@Override
	public Cancelable schedule(Runnable task, long delay) {
		SyncTask t = new SyncTask(runner, task);
		super.schedule(t, delay);
		return t;
	}
	
	private static class SyncTask extends TimerTask implements Cancelable {
		
		private final Runnable r;
		
		public SyncTask(TaskRunner runner, Runnable r) {
			this.r = () -> runner.runTask(r);
		}
		
		@Override
		public void run() {
			this.r.run();
		}
		
	}
	
}
