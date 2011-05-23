import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class MembershipManager{
	
    private ConcurrentHashMap<String, Peer> peers = new ConcurrentHashMap<String, Peer>();
	private final String mulitcastAddr;
	
	public MembershipManager(String multicastAddr, String nickname){
		this.mulitcastAddr = multicastAddr;
	}
	
	public String getMulitcastAddr() {
		return mulitcastAddr;
	}
	
	public Collection<Peer> getAllPeer() {
	    return Collections.unmodifiableCollection(this.peers.values());
	}
	
	public void receivedGday(Packet.GDay packet){
		this.peers.get(packet.nickname).receivedGday();
	}
}
