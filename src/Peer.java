import java.net.InetAddress;


public class Peer {
    private final InetAddress address;
    private final int port;
    
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
}
