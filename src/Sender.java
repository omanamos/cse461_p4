import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Sender {
    private static final int SOCKET_TIMEOUT_MILLIS = 10000;
    private MembershipManager manager;
    private DatagramSocket socket;
    private String nickname;

	public Sender(MembershipManager manager, DatagramSocket sock, String nickname){
		this.manager = manager;
		this.socket = sock;
		this.nickname = nickname;
		
		// TODO: spawn new thread that listens on STDIN
	}
	
	public void sendSays(String nickname, String message) {
	    Peer peer = manager.getPeer(nickname);
	    if(peer == null) {
	        System.err.println("unknown peer!: " + nickname);
	        return;
	    }
	    
	    byte[] payload = new Packet.Says(nickname, sequenceNumber, message);
	    packet = new DatagramPacket(buf, buf.length, peer.getAddress(), peer.getPort());
	    socket.send();
	}
}
