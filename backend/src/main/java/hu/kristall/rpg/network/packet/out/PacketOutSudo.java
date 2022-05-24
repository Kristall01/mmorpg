package hu.kristall.rpg.network.packet.out;

public class PacketOutSudo extends PacketOut {
	
	String text;
	
	private PacketOutSudo() {
		super("sudo");
	}
	
	public PacketOutSudo(String text) {
		this();
		this.text = text;
	}
}
