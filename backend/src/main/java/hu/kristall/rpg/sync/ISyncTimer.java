package hu.kristall.rpg.sync;

import java.util.Date;
import java.util.function.Consumer;

public interface ISyncTimer {
	
	Cancelable schedule(Consumer<Cancelable> task, long delay, long period);
	Cancelable schedule(Consumer<Cancelable> task, Date time);
	Cancelable schedule(Consumer<Cancelable> task, Date firstTime, long period);
	Cancelable scheduleAtFixedRate(Consumer<Cancelable> task, long delay, long period);
	Cancelable scheduleAtFixedRate(Consumer<Cancelable> task, Date firstTime, long period);
	Cancelable schedule(Consumer<Cancelable> task, long delay);
	
}
