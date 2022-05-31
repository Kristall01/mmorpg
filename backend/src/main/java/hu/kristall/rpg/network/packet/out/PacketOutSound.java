package hu.kristall.rpg.network.packet.out;

public class PacketOutSound extends PacketOut {
	
	String soundID;
	
	private PacketOutSound() {
		super("sound");
	}
	
	public PacketOutSound(String soundID) {
		this();
		this.soundID = soundID;
	}
	
}
