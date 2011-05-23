import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

// TODO: does "reliability" entail in-order delivery? What if a peer is misbehaving by sending too many packets?

public class Sender {
    private static final int TIMEOUT_MILLIS = 10000;
    private Timer timer = new Timer();
    private MembershipManager manager;
    private DatagramSocket socket;
    private String ourNickname;
    private int nextSequenceNumber; // NOT A LONG
    
    // Sequence number -> Pending timeouts. Sequence numbers are unique across messages and across peers.
    private Map<Integer, Retransmitter> pendingYeahs = 
    		new HashMap<Integer, Retransmitter>();

	public Sender(MembershipManager manager, DatagramSocket sock, String nickname) {
		this.manager = manager;
		this.socket = sock;
		this.ourNickname = nickname;
		nextSequenceNumber = 0;
		
		new KeyboardListener();
	}
	
	public void recievedYeah(Packet.Yeah yeahPkt) {
		pendingYeahs.remove(yeahPkt.sequenceNumber);
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
	    
	    for(Peer peer : manager.getAllPeer()) {
	    	int sequenceNumber = nextSequenceNumber++;
	    	byte[] payload = new Packet.Says(ourNickname, sequenceNumber, message).toBytes();
	    	// stop and wait for each peer
	    	DatagramPacket packet = new DatagramPacket(payload, payload.length, peer.getAddress(), peer.getPort());
	    	socket.send(packet);
	
	    	timer.schedule(new Retransmitter(sequenceNumber, packet), TIMEOUT_MILLIS);
	    }
	}
	

    public class Retransmitter extends TimerTask {
    	public static final int MAX_RETRIES = 5;
    	private int numberRetransmits = 0;
    	private DatagramPacket outgoingPacket;
		private int sequenceNumber;
    	
    	public Retransmitter(int sequenceNumber, DatagramPacket outgoingPacket) {
    		this.numberRetransmits = 0;
    		this.sequenceNumber = sequenceNumber;
    		this.outgoingPacket = outgoingPacket;
    	}
    	
        public void run() {
        	numberRetransmits += 1;
        	// if the YEAH hasn't been received...
        	if(pendingYeahs.containsKey(sequenceNumber)) {
        		if(numberRetransmits < MAX_RETRIES) {
	        		try {
						socket.send(outgoingPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
	        		timer.schedule(this, TIMEOUT_MILLIS);
        		} else {
        			pendingYeahs.remove(sequenceNumber);
        		}
        	}
        }
    }
}
