package hu.kristall.rpg.network.packet.out;

public class PacketOutDisconnect extends PacketOut {
	
	private String reason;
	
	public PacketOutDisconnect(String reason) {
		super("disconnect");
		this.reason = reason;
	}
	
	public PacketOutDisconnect() {
		super("Ki lettél rúgva a szerverről.");
	}
}
