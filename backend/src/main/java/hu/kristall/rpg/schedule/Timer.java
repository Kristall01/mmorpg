package hu.kristall.rpg.schedule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Timer {
	
	private Thread t;
	private ExecutorService service;
	private LinkedList<TimeConsumer> tickConsumers = new LinkedList<>();
	private long accumulatedTime, sleepDelay;
	
	
	public Timer(long sleepPrecisionDelayMs) {
		this.sleepDelay = sleepPrecisionDelayMs;
		
		service = Executors.newCachedThreadPool();
		t = new Thread(this::run);
		t.start();
	}
	
	private void tickConsumers(Iterable<TimeConsumer> iterable, long timeProgress) {
		Iterator<TimeConsumer> it = iterable.iterator();
		while(it.hasNext()) {
			TimeConsumer consumer = it.next();
			if(consumer.readyToRemove()) {
				it.remove();
			}
			consumer.tick(timeProgress);
		}
	}
	
	public void stop() {
		t.interrupt();
		service.shutdown();
	}
	
	private void run() {
		long countStart = System.nanoTime();
		while (true) {
			long now = System.nanoTime();
			long diff = now - countStart;
			accumulatedTime += diff;
			countStart = now;
			
			tickConsumers(tickConsumers, diff);
			
			try {
				Thread.sleep(sleepDelay);
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public TimeConsumer scheduleRepeatingTask(long interval, Runnable task) {
		RepeatingTask rtask = new RepeatingTask(this,interval, task);
		tickConsumers.add(rtask);
		return rtask;
	}
	
	public long getTimeAccumulated() {
		return accumulatedTime;
	}
	
	public void runTaskAsync(Runnable taskToRun) {
		try {
			service.submit(taskToRun);
		}
		catch (Exception ignored) {}
	}
	
}
