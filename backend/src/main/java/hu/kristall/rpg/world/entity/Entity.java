package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.*;
import hu.kristall.rpg.world.FloatingItem;
import hu.kristall.rpg.world.Inventory;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.path.Path;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Entity {
	
	private int entityID;
	private EntityType type;
	private final World world;
	private double speed;
	private boolean removed = false;
	private String name;
	private double hp;
	private double maxHp;
	private boolean alive = true;
	protected Inventory inventory;
	
	public Entity(World world, EntityType type, int entityID, double speed, double HP, double maxHp) {
		this.type = type;
		this.world = world;
		this.entityID = entityID;
		this.speed = speed;
		this.hp = HP;
		this.maxHp = maxHp;
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public abstract Position getPosition();
	
	public String getName() {
		return name;
	}
	
	public double getHp() {
		return hp;
	}
	
	public boolean heal(double amount) {
		if(amount <= 0) {
			world.getLogger().error("heal amount must be positive");
			return false;
		}
		return setHp(getHp()+amount);
	}
	
	public double getMaxHp() {
		return maxHp;
	}
	
	protected boolean setHp(final double baseAmount) {
		double amount = baseAmount;
		if(amount > getMaxHp()) {
			amount = getMaxHp();
		}
		if(amount <= 0) {
			amount = 0;
		}
		if(this.hp == amount) {
			return false;
		}
		handleHpChange(baseAmount - this.hp);
		this.hp = amount;
		world.broadcastPacket(new PacketOutHpChange(this.getID(), this.getHp()));
		if(amount == 0) {
			this.kill();
		}
		return true;
	}
	
	public void kill() {
		this.alive = false;
		this.remove();
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	protected void handleHpChange(double amount) {}
	
	public double damage(double amount) {
		if(amount <= 0) {
			world.getLogger().error("Entity::damage() got negative amount of damage");
			return 0;
		}
		setHp(getHp() - amount);
		return amount;
	}
	
	public double attack(Entity entity, double damage) {
		if(entity.equals(this)) {
			//cannot attack self
			return 0;
		}
		return entity.damage(damage);
	}
	
	public void setName(String name) {
		this.name = name;
		world.broadcastPacket(new PacketOutEntityRename(this));
	}
	
	public EntityType type() {
		return type;
	}
	
	public int getID() {
		return entityID;
	}
	
	public World getWorld() {
		return world;
	}
	
	public abstract Path getLastPath();
	
	public abstract void move(Position to);
	
	public abstract void stop();
	
	public abstract void teleport(Position pos);
	
	public double getSpeed() {
		return this.speed;
	}
	
	public void remove() {
		this.removed = true;
		world.cleanRemovedEntity(this);
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void setSpeed(double newSpeed) {
		this.speed = newSpeed;
		this.world.broadcastPacket(new PacketOutEntityspeed(this));
		this.move(getLastPath().getTarget());
	}
	
	public void sendStatusFor(PlayerConnection conn) {
		conn.sendPacket(new PacketOutSpawnEntity(this));
		if(getLastPath().getPosiFn().moving()) {
			conn.sendPacket(new PacketOutMoveentity(this));
		}
		conn.sendPacket(new PacketOutHpChange(this));
		if(getName() != null) {
			conn.sendPacket(new PacketOutEntityRename(this));
		}
	}
	
	public void pickupNearbyItems(double radius) {
		Collection<FloatingItem> worldItems = world.getItems();
		ArrayList<FloatingItem> pickupTargets = new ArrayList<>();
		Position entityPosition = getPosition();
		inventory.setBroadcastStopped(true);
		for (FloatingItem floatingItem : worldItems) {
			if(Position.distance(entityPosition, floatingItem.getPosition()) < radius) {
				pickupTargets.add(floatingItem);
				inventory.addItem(floatingItem.getItem(), 1);
			}
		}
		for (FloatingItem pickupTarget : pickupTargets) {
			pickupTarget.remove();
		}
		inventory.setBroadcastStopped(false);
		if(!pickupTargets.isEmpty()) {
			inventory.broadcastUpdate();
		}
	}
	
}
