package hu.kristall.rpg.world;

import java.util.function.Supplier;

public class IdIncrementer implements Supplier<Integer> {

	private int next;
	
	@Override
	public Integer get() {
		return next++;
	}
	
}
