import java.io.IOException;
import java.net.DatagramSocket;


public class Main {

	public static void main(String[] args) throws IOException{
		String multicastAddr = args[0];
		int port = Integer.parseInt(args[1]);
		String nickname = args[2];
		
		DatagramSocket unicastSock = new DatagramSocket(port);
		MembershipManager m = new MembershipManager(multicastAddr, nickname);
		new Sender(m, unicastSock, nickname);
		new Receiver(unicastSock);
	}
}
