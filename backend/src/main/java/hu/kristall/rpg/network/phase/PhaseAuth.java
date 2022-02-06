package hu.kristall.rpg.network.phase;

import hu.kristall.rpg.network.RawConnection;
import hu.kristall.rpg.network.packet.out.PacketOutAuthenticated;

public class PhaseAuth implements Phase {
	
	private RawConnection conn;
	
	public PhaseAuth(RawConnection conn) {
		this.conn = conn;
	}
	
	public void processMessage(String message) {
		PhasePlay phasePlay = new PhasePlay(conn, message);
		phasePlay.sendPacket(new PacketOutAuthenticated());
		conn.phase = phasePlay;
	}
	
	@Override
	public void handleDisconnect() {
		//who cares
	}
	
}
