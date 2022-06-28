package hu.kristall.rpg.world.item.interact;

import hu.kristall.rpg.world.PotionEffect;
import hu.kristall.rpg.world.PotionEffectType;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.item.InteractHandler;

public class StrengthPotion implements InteractHandler {
	
	private long effectTime;
	private double damageBoost;
	
	public StrengthPotion(long effectTime, double damageBoost) {
		this.effectTime = effectTime;
		this.damageBoost = damageBoost;
	}
	
	@Override
	public boolean interact(EntityHuman human) {
		human.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, (int)damageBoost, effectTime));
		return true;
	}
	
}
