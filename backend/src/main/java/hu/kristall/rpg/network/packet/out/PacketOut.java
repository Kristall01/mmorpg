package hu.kristall.rpg.network.packet.out;

public class PacketOut {
	
	private final transient String type;
	
	public PacketOut(String type) {
		this.type = type;
	}
	
	public String type() {
		return this.type;
	}
	
	public Object serializedData() {
		return this;
	}
	
}
