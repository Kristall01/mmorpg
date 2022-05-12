package hu.kristall.rpg.network.packet.out;

public class PacketOutChat extends PacketOut {
	
	private String message;

	private PacketOutChat() {
		super("chat");
	}
	
	public PacketOutChat(String message) {
		this();
		this.message = message;
	}
	
}
