package hu.kristall.rpg.network.packet.out;

import com.google.gson.JsonElement;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.cozy.ClothPack;

import java.util.List;

public class PacketOutChangeClothes extends PacketOut {
	
	private JsonElement clothes;
	private int id;
	
	public PacketOutChangeClothes(int entityID, ClothPack pack) {
		super("clothes");
		this.id = entityID;
		this.clothes = pack.serializeJson();
	}
	
	public PacketOutChangeClothes(EntityHuman e) {
		this(e.getID(), e.getClothes());
	}
}
