package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.LabelType;

public class PacketOutLabelFor extends PacketOut {

	String text;
	int labelType;
	int entityID;
	
	public PacketOutLabelFor(int id, LabelType type, String text) {
		super("labelFor");
		this.entityID = id;
		this.text = text;
		this.labelType = type.code;
	}
	
}
