package hu.kristall.rpg.world.entity;

import hu.kristall.rpg.Position;
import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.network.packet.out.*;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.LabelType;
import hu.kristall.rpg.world.World;
import hu.kristall.rpg.world.WorldPlayer;
import hu.kristall.rpg.world.entity.cozy.Cloth;
import hu.kristall.rpg.world.entity.cozy.ClothPack;
import hu.kristall.rpg.world.path.ConstantPosition;
import hu.kristall.rpg.world.path.Path;

import java.util.List;

public class EntityHuman extends Entity {
	
	private WorldPlayer worldPlayer;
	private Path lastPath;
	private ClothPack clothes = new ClothPack(Cloth.SUIT, Cloth.PANTS_SUIT, Cloth.SHOES);
	
	public EntityHuman(World world, int entityID, Position startPosition) {
		super(world, EntityType.HUMAN,  entityID);
		this.lastPath = new Path(startPosition, List.of(startPosition, startPosition), new ConstantPosition(startPosition), System.nanoTime());
	}
	
	public WorldPlayer getWorldPlayer() {
		return worldPlayer;
	}
	
	public void setWorldPlayer(WorldPlayer worldPlayer) {
		this.worldPlayer = worldPlayer;
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
	public void kill() {
		try {
			worldPlayer.getAsyncPlayer().sync(p -> {
				if(p != null) {
					p.sendMessage("Meghaltál!");
					p.scheduleWorldChange(p.getServer().getWorldsManager().getDefaultWorld());
				}
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//server won't be shut down while players are playing
			e.printStackTrace();
		}
		super.kill();
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
}
