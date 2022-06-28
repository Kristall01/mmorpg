package hu.kristall.rpg.network.packet.out;

import com.google.gson.JsonElement;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.cozy.ClothPack;

public class PacketOutChangeClothes extends PacketOut {
	
	private JsonElement clothes;
	private int id;
	
	private PacketOutChangeClothes() {
		super("clothes");
	}
	
	public PacketOutChangeClothes(int entityID, ClothPack pack) {
		this();
		this.id = entityID;
		this.clothes = pack.serializeJson();
	}
	
	public PacketOutChangeClothes(EntityHuman e) {
		this(e.getID(), e.getClothes());
	}
	
}
