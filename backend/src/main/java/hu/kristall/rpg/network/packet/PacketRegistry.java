package hu.kristall.rpg.network.packet;

import hu.kristall.rpg.network.packet.in.PacketIn;

public class PacketRegistry<T extends PacketIn> {
	
	/*private Map<ConnectionPhase, Map<String, Class<? extends PacketIn>>> phaseMap = new HashMap<>();
	private Map<String, Class<? extends PacketIn>> generalMap = new HashMap<>();
	
	private PacketRegistry() {}
	
	public static PacketRegistry baseRegistry() {
		PacketRegistry reg = new PacketRegistry();
		PacketRegistry.Builder builder = new PacketRegistry.Builder();
		
		//GENERAL
		builder.registerPacket(null, PacketInPlayPing.class, "ping");
		
		//AUTH
		builder.registerPacket(ConnectionPhase.AUTHENTICATE, PacketInAuthLogin.class, "login");
		
		//PLAY
		builder.registerPacket(ConnectionPhase.PLAY, PacketInPlayChat.class, "chat");
		
		return builder.build();
	}
	
	public Class<? extends PacketIn> getPacket(String type, ConnectionPhase phase) {
		if(phase == null) {
			return getGeneralPacket(type);
		}
		Class<? extends PacketIn> packetGenerator = phaseMap.computeIfAbsent(phase, k -> new HashMap<>()).get(type);
		return packetGenerator == null ? getGeneralPacket(type) : packetGenerator;
	}
	
	public Class<? extends PacketIn> getGeneralPacket(String type) {
		return generalMap.get(type);
	}
	
	public static class Builder {
		
		private Map<ConnectionPhase, Map<String, Class<? extends PacketIn>>> phaseMap = new HashMap<>();
		private Map<String, Class<? extends PacketIn>> generalMap = new HashMap<>();
		
		public void registerPacket(ConnectionPhase phase, Class<? extends PacketIn> in, String type) {
			Map<String, Class<? extends PacketIn>> targetMap = null;
			if(phase == null) {
				targetMap = generalMap;
			}
			else {
				targetMap = phaseMap.computeIfAbsent(phase, k -> new HashMap<>());
			}
			targetMap.put(type,in);
		}
		
		public PacketRegistry build() {
			PacketRegistry reg = new PacketRegistry();
			reg.generalMap = new HashMap<>(this.generalMap);
			reg.phaseMap = new HashMap<>(this.phaseMap);
			return reg;
		}
	
	}*/
	
}
