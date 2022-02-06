package hu.kristall.rpg.network;

import hu.kristall.rpg.network.packet.in.PacketIn;

import java.util.HashMap;
import java.util.Map;

public class PacketMap <T extends PacketIn> {
	
	private Map<String, Class<? extends T>> map;
	
	private PacketMap(Map<String, Class<? extends T>> oldMap) {
		this.map = new HashMap<>(oldMap);
	}
	
	public Class<? extends T> get(String type) {
		return map.get(type);
	}
	
	public static class Builder<U extends PacketIn> {
		
		private Map<String, Class<? extends U>> builderMap = new HashMap<>();
		
		public void register(String type, Class<? extends U> value) {
			this.builderMap.put(type, value);
		}
		
		public PacketMap<U> build() {
			return new PacketMap<>(this.builderMap);
		}
		
	}
	

}
