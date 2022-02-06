package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.network.packet.out.PacketOutPong;

public class PacketInPlayPing extends PacketInPlay {
	
	@Override
	public void execute() {
		sender.getCreator().sendPacket(new PacketOutPong());
	}
	
}
