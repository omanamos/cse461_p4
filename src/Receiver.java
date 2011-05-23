import java.io.*;
import java.net.*;

public class Receiver {
    private DatagramSocket socket;
    private MembershipManager manager;
    
    public Receiver(MembershipManager manager, DatagramSocket sock, Sender sender) throws IOException {
        this.socket = sock;
        this.manager = manager;
        //socket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        new UDPListenerThread().run();
    }
    
	public class UDPListenerThread extends Thread {

	    public void run() {
	        while (true) {
	            try {
	                byte[] buf = new byte[256];

	                // receive request
	                DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                socket.receive(packet);
	                
	                Packet type = Packet.getType(packet.getData());
	                
	                switch(type) {
	                case SAYS: 
	                    // TODO: print to screen
                        // TODO: send YEAH reply
	                    // send the response to the client at "address" and "port"
	                    InetAddress address = packet.getAddress();
	                    int port = packet.getPort();
	                    packet = new DatagramPacket(buf, buf.length, address, port);
	                    socket.send(packet);
	                    break;
	                case YEAH:
	                	
	                    break;
	                case GDAY:
	                	manager.receivedGday(new Packet.GDay(packet.getData()));
	                case GBYE:
	                	manager.recievedGbye(new Packet.GBye(packet.getData()));
	                default: 
	                    System.err.println("Unknown Packet contents! " + packet.getData().toString());
	                    continue;
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	                break;
	            }
	        }
	        socket.close();
	    }
	}
}
