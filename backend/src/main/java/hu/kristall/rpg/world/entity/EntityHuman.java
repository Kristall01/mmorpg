package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.ThreadCloneable;
import hu.kristall.rpg.WorldPosition;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.PacketOutChangeClothes;
import hu.kristall.rpg.network.packet.out.PacketOutEntityTeleport;
import hu.kristall.rpg.network.packet.out.PacketOutLabelFor;
import hu.kristall.rpg.network.packet.out.PacketOutMoveentity;
import hu.kristall.rpg.network.packet.out.inventory.PacketOutSetInventory;
import hu.kristall.rpg.persistence.SavedItem;
import hu.kristall.rpg.persistence.SavedItemStack;
import hu.kristall.rpg.persistence.SavedPlayer;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.*;
import hu.kristall.rpg.world.entity.cozy.ClothPack;
import hu.kristall.rpg.world.path.ConstantPosition;
import hu.kristall.rpg.world.path.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityHuman extends Entity implements ThreadCloneable<SavedPlayer> {
	
	private WorldPlayer worldPlayer;
	private Path lastPath;
	private ClothPack clothes;
	
	private EntityHuman(World world, int entityID, Position startPosition, double hp, ClothPack clothes, Map<Item, Integer> items) {
		super(world, EntityType.HUMAN, entityID, 2.0, hp, 100);
		this.inventory = new Inventory(this, items);
		this.clothes = clothes;
		this.lastPath = new Path(startPosition, List.of(startPosition, startPosition), new ConstantPosition(startPosition), System.nanoTime());
	}
	
	public EntityHuman(World world, int entityID, Position startPosition) {
		this(world, entityID, startPosition, 100, ClothPack.suit, new HashMap<>());
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
				m = Material.valueOf(savedItem.type);
			}
			catch (Exception ex) {
				world.getLogger().warn("failed to load item of type '" + savedItem.type+'\'');
				continue;
			}
			Item it = new Item(m, savedItem.name);
			Integer n = items.get(it);
			if(n == null) {
				n = stack.amount;
			}
			else {
				n += stack.amount;
			}
			items.put(it, n);
		}
		return new EntityHuman(world, entityID, pos, savedPlayer.hp, savedPlayer.clothes, items);
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
	
	@Override
	public Position getPosition() {
		return lastPath.getPosiFn().getCurrentLocation();
	}
	
	@Override
	public Path getLastPath() {
		return lastPath;
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
		to = getWorld().fixValidate(to);
		long now = System.nanoTime();
		this.lastPath = this.getWorld().interpolatePath(getPosition(), to, getSpeed(), now);
		getWorld().broadcastPacket(new PacketOutMoveentity(this));
	}
	
	@Override
	public void stop() {
		teleport(getPosition(), false);
	}
	
	public void teleport(Position pos, boolean instant) {
		this.lastPath = getWorld().idlePath(pos);
		getWorld().broadcastPacket(new PacketOutEntityTeleport(pos.getX(), pos.getY(), getID(), instant));
	}
	
	@Override
	public void teleport(Position pos) {
		teleport(pos, true);
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
}
