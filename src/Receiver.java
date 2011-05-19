import java.io.*;
import java.net.*;

public class Receiver {

	public Receiver(DatagramSocket sock) throws IOException{
		new UDPListenerThread(sock, this);
	}
	
	public void parseMessage(){
		
	}
	
	public class UDPListenerThread extends Thread {

	    protected DatagramSocket socket = null;
	    private Receiver rec;

	    public UDPListenerThread(DatagramSocket sock, Receiver rec) throws IOException {
	        socket = sock;
	        this.rec = rec;
	    }

	    public void run() {
	        while (true) {
	            try {
	                byte[] buf = new byte[256];

	                // receive request
	                DatagramPacket packet = new DatagramPacket(buf, buf.length);
	                socket.receive(packet);
	                
	                packet.getData();
	                
	                // send the response to the client at "address" and "port"
	                InetAddress address = packet.getAddress();
	                int port = packet.getPort();
	                packet = new DatagramPacket(buf, buf.length, address, port);
	                socket.send(packet);
	            } catch (IOException e) {
	                e.printStackTrace();
	                break;
	            }
	        }
	        socket.close();
	    }
	}
}
