package hu.kristall.rpg.network.packet.in;

import hu.kristall.rpg.network.WebsocketPlayerConnection;

public abstract class PacketIn {
	
	private transient WebsocketPlayerConnection sender;
	
	public WebsocketPlayerConnection getSender() {
		return sender;
	}
	
	public void setSender(WebsocketPlayerConnection sender) {
		this.sender = sender;
	}
	
	public abstract void execute();
	
}
