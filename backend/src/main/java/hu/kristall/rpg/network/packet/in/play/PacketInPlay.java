package hu.kristall.rpg.network.packet.in.play;

import hu.kristall.rpg.Player;
import hu.kristall.rpg.network.packet.in.PacketIn;

public abstract class PacketInPlay extends PacketIn {
	
	public Player sender;
	
}
