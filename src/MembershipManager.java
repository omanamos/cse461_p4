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
	
	public Peer getPeer(String nickname) {
	    return peers.get(nickname);
	}
}
