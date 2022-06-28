package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.ThreadCloneable;
import hu.kristall.rpg.Utils;
import hu.kristall.rpg.WorldPosition;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOutAttack;
import hu.kristall.rpg.network.packet.out.PacketOutChangeClothes;
import hu.kristall.rpg.network.packet.out.PacketOutLabelFor;
import hu.kristall.rpg.network.packet.out.PacketOutSound;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutOpenInventory;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSetInventory;
import hu.kristall.rpg.persistence.SavedPlayer;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.*;
import hu.kristall.rpg.world.entity.cozy.ClothPack;
import hu.kristall.rpg.world.inventory.MerchantInventory;
import hu.kristall.rpg.world.inventory.PlayerInventory;
import hu.kristall.rpg.world.inventory.WritableInventory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EntityHuman extends RegularMovingEntity implements ThreadCloneable<SavedPlayer> {
	
	private WorldPlayer worldPlayer;
	private ClothPack clothes;
	private Position moveTarget;
	
	private long channel = 0;
	private boolean NPC;
	
	private double weaponDamage = 5;
	private Map<PotionEffectType, PotionEffect> potionEffects = new HashMap<>();
	private Consumer<EntityHuman> interactHandler;
	
	private EntityHuman(World world, int entityID, Position startPosition, double hp, ClothPack clothes, Map<Item, Integer> items, boolean NPC) {
		super(world, EntityType.HUMAN, entityID, 2.0, hp, 100, startPosition);
		this.inventory = new PlayerInventory(this, items);
		this.clothes = clothes;
		this.NPC = NPC;
	}
	
	private void addSavedPotionEffect(PotionEffect effect) {
		potionEffects.put(effect.getType(), effect);
	}
	
	public boolean addPotionEffect(PotionEffect effect) {
		PotionEffectType type = effect.getType();
		PotionEffect oldEffect = potionEffects.get(type);
		if(oldEffect == null) {
			effect.extendTime();
			potionEffects.put(type, effect);
			return true;
		}
		if(oldEffect.getLevel() > effect.getLevel()) {
			return false;
		}
		else if(oldEffect.getLevel() < effect.getLevel()) {
			effect.extendTime();
			potionEffects.put(type, effect);
			return true;
		}
		else {
			oldEffect.extendTime();
			return true;
		}
	}
	
	public boolean hasPotionEffect(String potionEffecType) {
		PotionEffect effect = potionEffects.get(potionEffecType);
		if(effect == null) {
			return false;
		}
		return effect.isActive();
	}
	
	private boolean channel(long time) {
		long now = System.nanoTime();
		if(this.channel > now) {
			return true;
		}
		this.channel = now + time;
		return false;
	}
	
	private EntityHuman(World world, int entityID, Position startPosition, Consumer<EntityHuman> interactHandler) {
		this(world, entityID, startPosition, 100, ClothPack.suit, new HashMap<>(), true);
		this.interactHandler = interactHandler;
	}
	
	public static EntityHuman ofData(World world, int entityID, Position pos, Object data) {
		if(data == null) {
			return new EntityHuman(world, entityID, pos, e -> e.getWorld().broadcastMessage("§aNPC §7»§r Heyho"));
		}
		if(data instanceof MerchantInventory) {
			return new EntityHuman(world, entityID, pos, (e) -> {
				e.openInventory(((MerchantInventory) data).getID());
			});
		}
		if(!(data instanceof SavedPlayer)) {
			throw new IllegalArgumentException();
		}
		SavedPlayer savedPlayer = (SavedPlayer) data;
		Map<Item, Integer> items = new HashMap<>();
		for (Map.Entry<String, Integer> stack : savedPlayer.inventory.entrySet()) {
			Item it = world.getItemMap().getItem(stack.getKey()).generateItem();
			items.put(it, stack.getValue());
		}
		EntityHuman h = new EntityHuman(world, entityID, pos, savedPlayer.hp, savedPlayer.clothes, items, false);
		for (PotionEffect potionEffect : savedPlayer.potionEffects) {
			potionEffect.fixTime();
			if(potionEffect.isActive()) {
				h.addSavedPotionEffect(potionEffect);
			}
		}
		return h;
	}
	
	public boolean isNPC() {
		return NPC;
	}
	
	public WorldPlayer getWorldPlayer() {
		return worldPlayer;
	}
	
	public void setWorldPlayer(WorldPlayer worldPlayer) {
		this.worldPlayer = worldPlayer;
	}
	
	@Override
	public void setInventory(WritableInventory inventory) {
		super.setInventory(inventory);
		this.worldPlayer.getAsyncPlayer().connection.sendPacket(new PacketOutSetInventory(inventory));
	}
	
	public void setClothes(ClothPack clothes) {
		this.clothes = clothes;
		getWorld().broadcastPacket(new PacketOutChangeClothes(this));
	}
	
	public ClothPack getClothes() {
		return clothes;
	}
	
	@Override
	public void move(Position to) {
		
		moveTarget = to;
		if(channel(10_000_000)) {
			return;
		}
		super.move(to);
	}
	
	@Override
	public void kill() {
		try {
			setHp(getMaxHp());
			worldPlayer.getAsyncPlayer().sync(p -> {
				if(p != null) {
					p.sendMessage("Meghaltál!");
					p.scheduleWorldChange(new WorldPosition(p.getServer().getWorldsManager().getDefaultWorld(), null));
				}
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//server won't be shut down while players are playing
			e.printStackTrace();
		}
		//super.kill();
	}
	
	@Override
	protected void handleHpChange(double amount) {
		int fixedAmount = (int)Math.abs(Math.round(amount));
		LabelType type;
		if(amount < 0) {
			type = LabelType.DAMAGE;
		}
		else {
			type = LabelType.HEAL;
		}
		getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutLabelFor(this.getID(), type, Integer.toString(fixedAmount)));
	}
	
	public void openInventory(String inventoryID) {
		getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutOpenInventory(inventoryID));
	}
	
	@Override
	public void sendStatusFor(PlayerConnection conn) {
		super.sendStatusFor(conn);
		conn.sendPacket(new PacketOutChangeClothes(this));
	}
	
	@Override
	public SavedPlayer structuredClone() {
		return new SavedPlayer(this);
	}
	
	@Override
	public double attack(Entity entity, double damage) {
		double amount = super.attack(entity, damage);
		if(amount > 0) {
			getWorldPlayer().getAsyncPlayer().connection.sendPacket(new PacketOutLabelFor(entity.getID(), LabelType.DAMAGE, Integer.toString((int)Math.round(amount))));
		}
		return amount;
	}
	
	private Integer getPotionLevel(PotionEffectType type) {
		PotionEffect effect = potionEffects.get(type);
		if(effect == null || !effect.isActive()) {
			return null;
		}
		return effect.getLevel();
	}
	
	@Override
	public double damage(double amount, Entity source) {
		if(isNPC()) {
			if(source instanceof EntityHuman) {
				interactHandler.accept((EntityHuman) source);
			}
			return 0;
		}
		Integer level = getPotionLevel(PotionEffectType.DEFENCE);
		if(level != null) {
			amount *= (1-(level/100.0));
		}
		return super.damage(amount, source);
	}
	
	public void applyWeaponDamage(double weaponDamage) {
		this.weaponDamage = weaponDamage;
	}
	
	//0.5 sec
	public void attackTowards(Position p) {
		if(this.channel(400_000_000)) {
			return;
		}
		getWorld().broadcastPacket(new PacketOutSound("sword"));
		moveTarget = null;
		this.stop();
		getWorld().broadcastPacket(new PacketOutAttack(this, p));
		EntityHuman thisHuman = this;
		final WorldPlayer owner = this.getWorldPlayer();
		
		Position myPos = getPosition();
		
		Double rads = Position.rads(myPos, p);
		if(rads == null) {
			rads = 0d;
		}
		double treshold = Math.PI/4; //90 deg
		
		
		Collection<Entity> worldEntities = getWorld().getEntities();
		
		Entity[] attackTargets = new Entity[worldEntities.size()];
		int attackTargetsIndex = 0;
		
		for (Entity entity : worldEntities) {
			if(entity.equals(this)) {
				continue;
			}
			Position entityPosition = entity.getPosition();
			double dist = Position.distance(entityPosition, myPos);
			if(dist > 2) {
				continue;
			}
			if(dist < 0.5) {
				attackTargets[attackTargetsIndex++] = entity;
				continue;
			}
			Double entityRadsRaw = Position.rads(myPos, entityPosition);
			if(entityRadsRaw == null) {
				attackTargets[attackTargetsIndex++] = entity;
				continue;
			}
			double entityRads = entityRadsRaw;
			double diff = entityRads - rads % (Utils.PI2);
			if(diff < 0) {
				diff *= -1;
			}
			if(diff > Math.PI) {
				diff = Utils.PI2 - diff;
			}
			if(diff < treshold) {
				attackTargets[attackTargetsIndex++] = entity;
			}
		}
		double damage = weaponDamage;
		Integer potionLevel = getPotionLevel(PotionEffectType.STRENGTH);
		if(potionLevel != null) {
			damage += potionLevel;
		}
		for (int i = 0; i < attackTargetsIndex; i++) {
			this.attack(attackTargets[i], damage);
		}
		
		getWorld().getTimer().schedule(c -> {
			//this runs on world thread
			if(!owner.hasQuit() && moveTarget != null && owner.hasEntity() && owner.getEntity().equals(thisHuman)) {
				thisHuman.move(moveTarget);
			}
		}, 400);
	}
	
	public Collection<PotionEffect> getPotionEffects() {
		return Collections.unmodifiableCollection(potionEffects.values());
	}
}
