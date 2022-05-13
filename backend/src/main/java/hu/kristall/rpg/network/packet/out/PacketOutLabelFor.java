package hu.kristall.rpg.network.packet.out;

import hu.kristall.rpg.world.LabelType;

public class PacketOutLabelFor extends PacketOut {

	String text;
	int labelType;
	int entityID;
	
	
	private PacketOutLabelFor() {
		super("labelFor");
	}
	
	public PacketOutLabelFor(int id, LabelType type, String text) {
		this();
		this.entityID = id;
		this.text = text;
		this.labelType = type.code;
	}
	
}
