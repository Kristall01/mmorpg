package hu.kristall.rpg.sync;

import java.util.Date;

public interface ISyncTimer {
	
	Cancelable schedule(Runnable task, long delay, long period);
	Cancelable schedule(Runnable task, Date time);
	Cancelable schedule(Runnable task, Date firstTime, long period);
	Cancelable scheduleAtFixedRate(Runnable task, long delay, long period);
	Cancelable scheduleAtFixedRate(Runnable task, Date firstTime, long period);
	Cancelable schedule(Runnable task, long delay);
	
}
