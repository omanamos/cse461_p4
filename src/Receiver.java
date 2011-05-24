import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Receiver implements ManagerListener{
    private DatagramSocket socket;
    private MembershipManager manager;
    private Sender sender;
    private Map<User, Integer> lastSeqNumsReceived = new HashMap<User, Integer>();
    
    public Receiver(MembershipManager manager, DatagramSocket sock, Sender sender) throws IOException {
        this.socket = sock;
        this.manager = manager;
        this.sender = sender;
        new UDPListenerThread().start();
    }
    
    public void disconnect(User u){
    	this.lastSeqNumsReceived.remove(u);
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
		                	
		                	Packet.Says says = null; 
		                	try {
		                		says = new Packet.Says(packet.getData());
		                	} catch(IllegalArgumentException e) {
		                		System.err.println("Recieved illegal packet");
		                	}
		                	
		                	if(manager.isPeer(new User(says.nickname, packet.getAddress().toString()))){
		                		User u = new User(says.nickname, packet.getAddress().toString());
			                	Integer lastSeqNum = lastSeqNumsReceived.get(says.nickname);
			                	
			                	if(lastSeqNum == null || lastSeqNum <= says.sequenceNumber) {
			                		System.out.println(u.toString() + ": " + says.message);
			                		lastSeqNum = says.sequenceNumber;
			                	}
			                	lastSeqNumsReceived.put(u, lastSeqNum);
			                	
			                	try{
			                		buf = new Packet.Yeah(lastSeqNum).toBytes();
			                		packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
				                    socket.send(packet);
			                	}catch(Exception e){}
		                	}
		                    break;
		                case YEAH:
		                	try{
		                		try {
		                		sender.recievedYeah(new Packet.Yeah(packet.getData()));
		                		} catch(IllegalArgumentException e) {
		                			System.err.println("Received malformed YEAH packet");		                			
		                		}
		                	}catch(Exception e){} 
		                    break;
		                case GDAY:
		                	try {
		                		manager.receivedGday(new Peer(packet.getAddress(), packet.getPort()), new Packet.GDay(packet.getData()));	
		                		} catch(IllegalArgumentException e) {
		                			System.err.println("Received malformed GDAY packet");		                			
		                		}
		                	break;
		                case GBYE:
		                	try {
			                	manager.recievedGbye(packet.getAddress().toString(), new Packet.GBye(packet.getData()));
		                		} catch(IllegalArgumentException e) {
		                			System.err.println("Received malformed GBYE packet");		                			
		                		}
		                	break;
		                default: 
		                    System.err.println("Unknown Packet contents! " + packet.getData().toString());
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
