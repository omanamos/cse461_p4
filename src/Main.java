import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;


public class Main {

	public static void main(String[] args) throws IOException{
		String multicastAddr = args[0];
		int port = Integer.parseInt(args[1]);
		String nickname = args[2];
		
		MulticastSocket socket = new MulticastSocket(port);
		MembershipManager m = new MembershipManager(multicastAddr, nickname);
		new Sender(m, socket, nickname);
		new Receiver(socket);
	}
}
