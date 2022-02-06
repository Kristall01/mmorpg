package hu.kristall.rpg.network.packet.out;

public class PacketOutPong extends PacketOut{
	
	private long time;
	
	public PacketOutPong() {
		super("pong");
		this.time = System.nanoTime();
	}
}
