package hu.kristall.rpg.world.item.interact;

import hu.kristall.rpg.world.PotionEffect;
import hu.kristall.rpg.world.PotionEffectType;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.item.InteractHandler;

public class DefencePotion implements InteractHandler {
	private long effectTime;
	private double defenceBoost;
	
	public DefencePotion(long effectTime, double defenceBoost) {
		this.effectTime = effectTime;
		this.defenceBoost = defenceBoost;
	}
	
	@Override
	public boolean interact(EntityHuman human) {
		human.addPotionEffect(new PotionEffect(PotionEffectType.DEFENCE, (int)defenceBoost, effectTime));
		return true;
	}
	
}
