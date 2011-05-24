import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentHashMap;

public class MembershipManager{
	
    private ConcurrentHashMap<User, Peer> peers = new ConcurrentHashMap<User, Peer>();

	private final MulticastSocket socket;
	private final String addr;
	private final User user;
	private final int port;

	private List<ManagerListener> listeners;
	
	public MembershipManager(MulticastSocket socket, String multicastAddr, int port, User user){
		this.addr = multicastAddr;
		this.socket = socket;
		this.user = user;
		this.port = port;
		this.listeners = new ArrayList<ManagerListener>();
		
		new GDayThread().start();
	}
	
	public String getMulitcastAddr() {
		return addr;
	}
	
	public boolean isPeer(User u){
		return this.peers.containsKey(u);
	}
	
	public Collection<Peer> getAllPeers() {
		long curEpoch = System.currentTimeMillis();
		Set<User> toRemove = new HashSet<User>();
		
		for(User nickname : this.peers.keySet())
			if(!nickname.equals(this.user) && this.peers.get(nickname).isExpired(curEpoch))
				toRemove.add(nickname);
		
		for(User u : toRemove){
			this.disconnect(u);
		}
		
	    return Collections.unmodifiableCollection(this.peers.values());
	}
	
	public Peer getPeer(String nickname){
		return this.peers.get(nickname);
	}
	
	public void receivedGday(Peer peer, Packet.GDay packet){
		User u = new User(packet.nickname, peer.getAddress().toString());
		if(!peers.containsKey(u)){
			this.peers.put(u, peer);
			System.out.println(u + " joined the chat from addr " + peer.getAddress());
		}
		
		this.peers.get(u).receivedGDay();
	}
	
	public void recievedGbye(String address, Packet.GBye packet){
		User u = new User(packet.nickname, address);
		this.disconnect(u);
	}
	
	public void registerListener(ManagerListener listener) {
		this.listeners.add(listener);
	}
	
	private void disconnect(User u){
		System.out.println(u + " left the chat.");
		this.peers.remove(u);
		
		for(ManagerListener listener : listeners)
			listener.disconnect(u);
	}
	
	private class GDayThread extends Thread {
		
		private static final long SEND_INTERVAL = 1000;
		private final byte[] gday;
		
		public GDayThread(){
			this.gday = new Packet.GDay(user.nickname).toBytes();
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
