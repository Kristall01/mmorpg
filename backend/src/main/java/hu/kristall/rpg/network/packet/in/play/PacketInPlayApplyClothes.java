package hu.kristall.rpg.network.packet.in.play;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hu.kristall.rpg.network.packet.out.PacketOutChat;
import hu.kristall.rpg.sync.Synchronizer;
import hu.kristall.rpg.world.entity.EntityHuman;
import hu.kristall.rpg.world.entity.cozy.Cloth;
import hu.kristall.rpg.world.entity.cozy.ClothColor;
import hu.kristall.rpg.world.entity.cozy.ClothPack;
import hu.kristall.rpg.world.entity.cozy.ColoredCloth;

public class PacketInPlayApplyClothes extends PacketInPlay {
	
	JsonArray clothes;
	
	@Override
	public void execute() {
		ColoredCloth[] clothArray = new ColoredCloth[clothes.size()];
		int i = 0;
		for (JsonElement clothRaw : clothes) {
			JsonObject cloth = clothRaw.getAsJsonObject();
			String clothID = cloth.get("type").getAsString();
			Cloth c = null;
			try {
				c = Cloth.valueOf(clothID);
			}
			catch (IllegalArgumentException ex) {
				getSender().sendPacket(new PacketOutChat("§cHiba: §4Nem létezik §c'" + clothID + "'§4 típusú ruha."));
				return;
			}
			ClothColor cc = null;
			String colorID = cloth.get("color").getAsString();
			try {
				cc = ClothColor.valueOf(colorID);
			}
			catch (IllegalArgumentException ex) {
				getSender().sendPacket(new PacketOutChat("§cHiba: §4Nem létezik §c'" + clothID + "'§4 típusú ruhaszín."));
				return;
			}
			clothArray[i++] = new ColoredCloth(c, cc);
		}
		ClothPack pack = new ClothPack(clothArray);
		try {
			getSender().getPlayer().getAsyncEntity().sync(e -> {
				if(e == null) {
					//player left world
					return;
				}
				EntityHuman h = e.getEntity();
				if(h == null) {
					//player is dead, went shaco ult, or something
					return;
				}
				h.setClothes(pack);
			});
		}
		catch (Synchronizer.TaskRejectedException e) {
			//network server won't accept packets from clients when the server is already shut down
			e.printStackTrace();
		}
	}
	
}
