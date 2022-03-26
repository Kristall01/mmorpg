package hu.kristall.rpg;

import java.util.function.Supplier;

public class IdGenerator<T> implements Supplier<GeneratedID<T>> {
	
	private int next;
	
	@Override
	public GeneratedID<T> get() {
		return new GeneratedID<T>(next++);
	}
	
}
