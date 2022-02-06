package hu.kristall.rpg;

import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.network.phase.PhasePlay;
import hu.kristall.rpg.world.entity.EntityPlayer;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player {
	
	private String name;
	private PhasePlay creator;
	private final AtomicReference<Synchronizer<EntityPlayer>> asyncEntity = new AtomicReference<>(null);
	private final Lock worldLock;
	
	public Player(PhasePlay creator, String name) {
		this.name = name;
		this.creator = creator;
		this.worldLock = new ReentrantLock();
	}
	
	public String getName() {
		return name;
	}
	
	public void sendMessage(String message) {
		creator.sendPacket(new PacketOutChat(message));
	}
	
	public boolean tryLockWorld() {
		return worldLock.tryLock();
	}
	
	public void unlockWorld() {
		worldLock.unlock();
	}
	
	public PhasePlay getCreator() {
		return creator;
	}
	
	public void setAsyncEntity(Synchronizer<EntityPlayer> asyncEntity) {
		this.asyncEntity.set(asyncEntity);
	}
	
	public Synchronizer<EntityPlayer> getAsyncEntity() {
		return this.asyncEntity.get();
	}
	
}
