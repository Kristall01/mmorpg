package hu.kristall.rpg;

import hu.kristall.rpg.network.PlayerConnection;
import hu.kristall.rpg.sync.Synchronizer;

public class AsyncPlayer extends Synchronizer<Player> {
	
	public final String name;
	public final PlayerConnection connection;
	
	public AsyncPlayer(Player p) {
		super(p, p.getServer());
		this.name = p.getName();
		this.connection = p.getConnection();
	}
	
}
