import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Sender {
    private static final int SOCKET_TIMEOUT_MILLIS = 10000;
    private MembershipManager manager;
    private DatagramSocket socket;
    private String ourNickname;
    private int nextSequenceNumber; // NOT A LONG
    
    private ConcurrentHashMap<Peer, Integer> pendingYeahs = new ConcurrentHashMap<Peer, Integer>();

	public Sender(MembershipManager manager, DatagramSocket sock, String nickname) {
		this.manager = manager;
		this.socket = sock;
		this.ourNickname = nickname;
		nextSequenceNumber = 0;
		
		// TODO: spawn new thread that listens on STDIN
		new KeyboardListener();
	}
	
	public class KeyboardListener extends Thread {
	    public void run() {
	    	Scanner sc = new Scanner(System.in);
	        while (true) {
	        	// TODO: factor this out so that we can test it
	        	System.out.print("> ");
	        	String message = sc.next();
            	try {
					sendSays(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	public void sendSays(String message) throws IOException {
	    // Send to all receivers
	    // nickname of the /sender/, not the receiver
	    byte[] payload = new Packet.Says(ourNickname, nextSequenceNumber++, message).toBytes();
	    
	    for(Peer peer : manager.getAllPeers()) {
	    	// stop and wait for each peer
	    	DatagramPacket packet = new DatagramPacket(payload, payload.length, peer.getAddress(), peer.getPort());
	    	socket.send(packet);
	    }
	    
	    // TODO: register a Timeout
	}
}
