import java.net.InetAddress;


public class Peer {
	private static final long THIRTY_SECONDS = 30000;
	
    private final InetAddress address;
    private final int port;
    private long epoch;
    
    public Peer(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
    
    public InetAddress getAddress() {
        return address;
    }
    
    public int getPort() {
        return port;
    }
    
    public boolean isExpired(long curEpoch){
    	return curEpoch - epoch > THIRTY_SECONDS;
    }
    
    public synchronized void receivedGday(){
    	epoch = System.currentTimeMillis();
    }
}
