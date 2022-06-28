package hu.kristall.rpg.world.item.interact;

import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.item.InteractHandler;

public class HealthPotion implements InteractHandler {
	
	private double amount;
	
	public HealthPotion(double amount) {
		this.amount = amount;
	}
	
	@Override
	public boolean interact(EntityHuman human) {
		if(Math.abs(human.getHp() - human.getMaxHp()) < 1.0/128.0) {
			return false;
		}
		human.heal(amount);
		return true;
	}
	
}
