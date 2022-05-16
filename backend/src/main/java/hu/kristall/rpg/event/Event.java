package hu.kristall.rpg.event;

public class Event {
	
	public final String type;
	public final Object value;
	
	public Event(String type, Object value) {
		this.type = type;
		this.value = value;
	}
	
}
