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
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSetInventory;
import hu.kristall.rpg.persistence.SavedItem;
import hu.kristall.rpg.persistence.SavedItemStack;
import hu.kristall.rpg.persistence.SavedPlayer;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.*;
import hu.kristall.rpg.world.entity.cozy.ClothPack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EntityHuman extends RegularMovingEntity implements ThreadCloneable<SavedPlayer> {
	
	private WorldPlayer worldPlayer;
	private ClothPack clothes;
	private Position moveTarget;
	
	private long channel = 0;
	private boolean NPC;
	
	private EntityHuman(World world, int entityID, Position startPosition, double hp, ClothPack clothes, Map<Item, Integer> items, boolean NPC) {
		super(world, EntityType.HUMAN, entityID, 2.0, hp, 100, startPosition);
		this.inventory = new Inventory(this, items);
		this.clothes = clothes;
		this.NPC = NPC;
	}
	
	private boolean channel(long time) {
		long now = System.nanoTime();
		if(this.channel > now) {
			return true;
		}
		this.channel = now + time;
		return false;
	}
	
	public EntityHuman(World world, int entityID, Position startPosition) {
		this(world, entityID, startPosition, 100, ClothPack.suit, new HashMap<>(), true);
	}
	
	public static EntityHuman ofData(World world, int entityID, Position pos, Object data) {
		if(data == null) {
			return new EntityHuman(world, entityID, pos);
		}
		if(!(data instanceof SavedPlayer)) {
			throw new IllegalArgumentException();
		}
		SavedPlayer savedPlayer = (SavedPlayer) data;
		Map<Item, Integer> items = new HashMap<>();
		for (SavedItemStack stack : savedPlayer.inventory) {
			SavedItem savedItem = stack.item;
			Material m = null;
			try {
				m = Material.valueOf(savedItem.material);
			}
			catch (Exception ex) {
				world.getLogger().warn("failed to load item of material '" + savedItem.type+'\'');
				continue;
			}
			Item it = new Item(savedItem.type, m, savedItem.description, savedItem.flags);
			Integer n = items.get(it);
			if(n == null) {
				n = stack.amount;
			}
			else {
				n += stack.amount;
			}
			items.put(it, n);
		}
		return new EntityHuman(world, entityID, pos, savedPlayer.hp, savedPlayer.clothes, items, false);
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
	public void setInventory(Inventory inventory) {
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
	
	@Override
	public double damage(double amount) {
		if(isNPC()) {
			getWorld().broadcastMessage("§aNPC §7»§r Heyho");
			return 0;
		}
		return super.damage(amount);
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
			if(dist > 1) {
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
		for (int i = 0; i < attackTargetsIndex; i++) {
			this.attack(attackTargets[i], 5);
		}
		
		getWorld().getTimer().schedule(c -> {
			//this runs on world thread
			if(!owner.hasQuit() && moveTarget != null && owner.hasEntity() && owner.getEntity().equals(thisHuman)) {
				thisHuman.move(moveTarget);
			}
		}, 400);
	}
	
}
