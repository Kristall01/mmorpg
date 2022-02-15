package hu.kristall.rpg.network;

public interface NetworkConnection {
	
	void handleNetworkMessage(String message);
	void handleConnectionClose();
	void close(String reason);
	
}
