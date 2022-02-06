package hu.kristall.rpg.network.phase;

public interface Phase {
	
	void processMessage(String message);
	
	void handleDisconnect();
}
