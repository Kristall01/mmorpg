package hu.kristall.rpg.network.packet.out;

import java.util.Objects;

public class PacketOutDisconnect extends PacketOut {
	
	private String reason;
	
	public PacketOutDisconnect(String reason) {
		super("disconnect");
		this.reason = Objects.requireNonNullElse(reason, "Ki lettél rúgva a szerverről.");
	}
	
}
