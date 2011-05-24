import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentHashMap;

public class MembershipManager{
	
    private ConcurrentHashMap<String, Peer> peers = new ConcurrentHashMap<String, Peer>();

	private final MulticastSocket socket;
	private final String addr;
	private final String nickname;
	private final int port;
	
	public MembershipManager(MulticastSocket socket, String multicastAddr, int port, String nickname){
		this.addr = multicastAddr;
		this.socket = socket;
		this.nickname = nickname;
		this.port = port;
		
		new GDayThread().start();
	}
	
	public String getMulitcastAddr() {
		return addr;
	}
	
	public Collection<Peer> getAllPeers() {
		long curEpoch = System.currentTimeMillis();
		Set<String> toRemove = new HashSet<String>();
		
		for(String nickname : this.peers.keySet())
			if(!nickname.equals(this.nickname) && this.peers.get(nickname).isExpired(curEpoch))
				toRemove.add(nickname);
		
		for(String nickname : toRemove){
			System.out.println(nickname + " left the chat.");
			this.peers.remove(nickname);
		}
		
	    return Collections.unmodifiableCollection(this.peers.values());
	}
	
	public Peer getPeer(String nickname){
		return this.peers.get(nickname);
	}
	
	public void receivedGday(Peer peer, Packet.GDay packet){
		if(!peers.containsKey(packet.nickname)){
			this.peers.put(packet.nickname, peer);
			System.out.println(packet.nickname + " joined the chat from addr " + peer.getAddress());
		}
		
		this.peers.get(packet.nickname).receivedGDay();
	}
	
	public void recievedGbye(Packet.GBye packet){
		System.out.println(packet.nickname + " left the chat.");
		this.peers.remove(packet.nickname);
	}
	
	private class GDayThread extends Thread {
		
		private static final long SEND_INTERVAL = 10000;
		private final byte[] gday;
		
		public GDayThread(){
			this.gday = new Packet.GDay(nickname).toBytes();
		}
		
		public void run(){
			while(true) {
				try {
					// TODO: is this the right port?
					socket.send(new DatagramPacket(gday, gday.length, InetAddress.getByName(addr), port));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(SEND_INTERVAL);
				} catch (InterruptedException e) {}
            }
		}
	}
}
