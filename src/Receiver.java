import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Receiver {
    private DatagramSocket socket;
    private MembershipManager manager;
    private Sender sender;
    private Map<String, Integer> lastSeqNumsReceived = new HashMap<String, Integer>();
    
    public Receiver(MembershipManager manager, DatagramSocket sock, Sender sender) throws IOException {
        this.socket = sock;
        this.manager = manager;
        this.sender = sender;
        new UDPListenerThread().start();
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
		                	Packet.Says says = new Packet.Says(packet.getData());
		                	if(manager.isPeer(new User(says.nickname, packet.getAddress().toString()))){
			                	Integer lastSeqNum = lastSeqNumsReceived.get(says.nickname);
			                	
			                	if(lastSeqNum == null || lastSeqNum <= says.sequenceNumber) {
			                		System.out.println(Utils.boxify(says));
			                		lastSeqNum = says.sequenceNumber;
			                	}
			                	lastSeqNumsReceived.put(says.nickname, lastSeqNum);
			                	
			                	try{
			                		buf = new Packet.Yeah(lastSeqNum).toBytes();
			                		packet = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
				                    socket.send(packet);
			                	}catch(Exception e){}
		                	}
		                    break;
		                case YEAH:
		                	try{
		                		sender.recievedYeah(new Packet.Yeah(packet.getData()));
		                	}catch(Exception e){}
		                    break;
		                case GDAY:
		                	manager.receivedGday(new Peer(packet.getAddress(), packet.getPort()), new Packet.GDay(packet.getData()));
		                	break;
		                case GBYE:
		                	manager.recievedGbye(packet.getAddress().toString(), new Packet.GBye(packet.getData()));
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
