package hu.kristall.rpg.event;

import kotlin.collections.ArrayDeque;

import java.util.List;

public abstract class EventConsumer {
	
	private List<EventSubscription> subscriptions = new ArrayDeque<>();
	
	public void cancelSubscriptions() {
		for (EventSubscription subscription : subscriptions) {
			subscription.cancel();
		}
	}
	
	public abstract void handleEvent(Event event);
	
}
