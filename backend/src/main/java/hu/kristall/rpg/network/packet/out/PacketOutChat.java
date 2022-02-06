package hu.kristall.rpg.network.packet.out;

public class PacketOutChat extends PacketOut {
	
	private String message;
	
	public PacketOutChat(String message) {
		super("chat");
		this.message = message;
	}
	
}
