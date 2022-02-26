package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.entity.EntityHuman;

import java.util.List;

public class PacketOutChangeClothes extends PacketOut {
	
	private List<String> clothes;
	private int id;
	
	public PacketOutChangeClothes(int entityID, List<String> clothes) {
		super("clothes");
		this.id = entityID;
		this.clothes = clothes;
	}
	
	public PacketOutChangeClothes(EntityHuman e) {
		this(e.getID(), e.getClothes().serialize());
	}
}
