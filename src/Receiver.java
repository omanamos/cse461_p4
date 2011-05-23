import java.io.*;
import java.net.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Receiver {
    private static final int SOCKET_TIMEOUT_MILLIS = 10000;
    
    private DatagramSocket socket;
    private final Lock yeahLock = new ReentrantLock();
    private final Condition yeahArrived = yeahLock.newCondition();
    // needs to be global for multiple threads
    private Set<PendingYeah> receivedYeahs;
    
    public Receiver(DatagramSocket sock) throws IOException {
        this.socket = sock;
        //socket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        new UDPListenerThread(this);
    }
    
    // Called by sender object
    public void receiveYeah(PendingYeah pending) {
        yeahLock.lock();
        try {
            // TODO: implement timeouts
            while(!receivedYeahs.contains(pending)) {
                yeahArrived.await(SOCKET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            }
            // We received what we were looking for!
            receivedYeahs.remove(pending);
        } catch (InterruptedException e) {
            // XXX Resend SAYS
        } finally {
            yeahLock.unlock();
        }
    }
    
	public class UDPListenerThread extends Thread {
	    private Receiver rec;

	    public UDPListenerThread(Receiver rec) throws IOException {
	        this.rec = rec; // XXX is receiver ever used? no...
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
