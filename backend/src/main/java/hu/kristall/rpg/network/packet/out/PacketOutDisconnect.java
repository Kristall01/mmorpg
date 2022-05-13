package hu.kristall.rpg.network.packet.out;

import java.util.Objects;

public class PacketOutDisconnect extends PacketOut {
	
	private String reason;
	
	private PacketOutDisconnect() {
		super("disconnect");
	}
	
	public PacketOutDisconnect(String reason) {
		this();
		this.reason = Objects.requireNonNullElse(reason, "Ki lettél rúgva a szerverről.");
	}
	
}
