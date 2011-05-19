import java.io.*;
import java.net.*;

import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Receiver {
    public Receiver(DatagramSocket sock) throws IOException{
        new UDPListenerThread(sock, this);
    }
    
    private final Lock yeahLock = new ReentrantLock();
    private final Condition yeahArrived = yeahLock.newCondition();
    
    // needs to be global for multiple threads
    private Set<PendingYeah> receivedYeahs;
    
    // Called by sender object
    public void receiveYeah(PendingYeah pending) throws InterruptedException {
        yeahLock.lock();
        try {
            while(!receivedYeahs.contains(pending)) {
                yeahArrived.await();
            }
            // We received what we were looking for!
            receivedYeahs.remove(pending);
        } finally {
            yeahLock.unlock();
        }
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
	                    yeahLock.lock();
	                    try {
	                        receivedYeahs.add(new PendingYeah(
	                                new Peer(packet.getAddress(), packet.getPort()), 
	                                        new Packet.Yeah(packet.getData())));
	                        yeahArrived.signal();
	                    } finally {
	                        yeahLock.unlock();
	                    }
	                    break;
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
