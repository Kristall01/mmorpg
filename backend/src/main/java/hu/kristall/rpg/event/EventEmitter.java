package hu.kristall.rpg.event;

import hu.kristall.rpg.GeneratedID;
import hu.kristall.rpg.IdGenerator;

import java.util.HashMap;

public class EventEmitter {
	
	private HashMap<Integer, EventConsumer> listeners = new HashMap<>();
	private IdGenerator<EventConsumer> idGenerator = new IdGenerator<>();
	
	public Runnable addEventListeneer(EventConsumer consumer) {
		GeneratedID<EventConsumer> id = idGenerator.get();
		listeners.put(id.value, consumer);
		final int value = id.value;
		return () -> removeConsumer(value);
	}
	
	private void removeConsumer(int id) {
		listeners.remove(id);
	}
	
	public void broadcastEvent(Event event) {
		for (EventConsumer consumer : listeners.values()) {
			consumer.handleEvent(event);
		}
	}
	
}
